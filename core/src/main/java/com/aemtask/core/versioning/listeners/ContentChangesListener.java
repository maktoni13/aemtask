package com.aemtask.core.versioning.listeners;


import com.aemtask.core.versioning.config.ContentChangesVersioningConfig;
import com.aemtask.core.versioning.utils.NodeUtils;
import com.aemtask.core.versioning.utils.VersioningUtils;
import com.day.cq.commons.jcr.JcrConstants;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;
import javax.jcr.version.VersionManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Event listener for tracking node changes
 */
@Component(
        service = EventListener.class,
        immediate = true
)
public class ContentChangesListener implements EventListener {

    private static final String INFO_MSG_EVENT_PROCESSING_START = "Event processing start (iterator = %s, size = %s)";
    private static final String INFO_MSG_EVENT_LISTENER_REGISTERED =
            "Content changes listener has been registered (types = %s, path = %s, isDeep = %s, uuids = %s, node types = %s, noLocal = %s";
    private static final String INFO_MSG_START_PROCESSING_SUB_EVENT = "Start processing sub event (path = %s,  type = %s, info = %s";
    private static final String INFO_MSG_NEW_VERSION_CREATED = "New version created (path = %s)";
    private static final String ERROR_MSG_DEACTIVATING_EVENT_LISTENER = "Error deactivating of event listener";
    private static final String ERROR_MSG_TREATING_EVENTS = "Error while treating events";
    private static final String ERROR_MSG_SESSION_REGISTRATION = "Unable to register session";

    private List<String> VERSIONING_DETECT_TYPES = Arrays.asList(
            JcrConstants.JCR_PREDECESSORS,
            JcrConstants.JCR_ISCHECKEDOUT,
            JcrConstants.JCR_MIXINTYPES,
            JcrConstants.JCR_BASEVERSION,
            JcrConstants.JCR_VERSIONHISTORY);

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private Session observationSession;

    @Reference
    private NodeUtils nodeUtils;

    @Reference
    private VersioningUtils utils;

    @Reference
    private SlingRepository repository;

    @Reference
    private ContentChangesVersioningConfig versioningConfig;

    private boolean isVersioningEvent(Event event) throws RepositoryException {
        return (event.getType() == Event.PROPERTY_ADDED
                || event.getType() == Event.PROPERTY_CHANGED)
                && VERSIONING_DETECT_TYPES.stream().anyMatch(event.getPath()::endsWith);
    }

    @Override
    public void onEvent(EventIterator eventIterator) {
        LOGGER.info(String.format(INFO_MSG_EVENT_PROCESSING_START, eventIterator.toString(), eventIterator.getSize()));
        List<String> rootPageList = new ArrayList<>();
        while (eventIterator.hasNext()) {
            try {
                Event event = eventIterator.nextEvent();
                LOGGER.info(String.format(INFO_MSG_START_PROCESSING_SUB_EVENT, event.getPath(), event.getType(), event.getInfo()));
                Session session = repository.login(versioningConfig.getCredentials());
                if (isVersioningEvent(event)) {
                    session.logout();
                    continue;
                }
                Node node = session.getNodeByIdentifier(event.getIdentifier());
                Node rootPageNode = nodeUtils.getRootPageNode(node, utils.getPath());
                if (rootPageNode == null || rootPageList.contains(rootPageNode.getPath())) {
                    session.logout();
                    continue;
                }
                if (nodeUtils.isDescriptionPresent(rootPageNode)) {
                    NodeType[] nodeTypes = rootPageNode.getMixinNodeTypes();
                    if (Arrays.stream(nodeTypes).noneMatch(nodeType -> nodeType.isNodeType(NodeType.MIX_VERSIONABLE))) {
                        rootPageNode.addMixin(JcrConstants.MIX_VERSIONABLE);
                        session.save();
                    }
                    saveVersion(session, rootPageNode);
                }
                rootPageList.add(rootPageNode.getPath());
                session.logout();
            } catch (RepositoryException e) {
                LOGGER.error(ERROR_MSG_TREATING_EVENTS, e);
            }
        }
    }

    private void saveVersion(Session session, Node node) throws RepositoryException {
        VersionManager versionManager = session.getWorkspace().getVersionManager();
        versionManager.checkin(node.getPath());
        versionManager.checkout(node.getPath());
        LOGGER.info(String.format(INFO_MSG_NEW_VERSION_CREATED, node.getPath()));
    }

    @Activate
    public void activate() {
        try {
            observationSession = repository.login(versioningConfig.getCredentials());
            observationSession.getWorkspace()
                    .getObservationManager()
                    .addEventListener(
                            this,
                            utils.getEventTypes(),
                            utils.getPath(),
                            utils.getIsDeep(),
                            utils.getUuidArr(),
                            utils.getNodeTypes(),
                            utils.getNoLocal());
            LOGGER.info(String.format(INFO_MSG_EVENT_LISTENER_REGISTERED,
                    utils.getEventTypes(),
                    utils.getPath(),
                    utils.getIsDeep(),
                    Arrays.toString(utils.getUuidArr()),
                    Arrays.toString(utils.getNodeTypes()),
                    utils.getNoLocal()));
        } catch (RepositoryException e) {
            LOGGER.error(ERROR_MSG_SESSION_REGISTRATION, e);
        }
    }

    @Deactivate
    public void deactivate() {
        try {
            final ObservationManager observationManager = observationSession.getWorkspace().getObservationManager();
            if (observationManager != null) {
                observationManager.removeEventListener(this);
            }
        } catch (RepositoryException e) {
            LOGGER.error(ERROR_MSG_DEACTIVATING_EVENT_LISTENER, e);
        } finally {
            if (observationSession != null) {
                observationSession.logout();
            }
        }
    }

}
