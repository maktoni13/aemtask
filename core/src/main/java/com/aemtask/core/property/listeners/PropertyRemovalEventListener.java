package com.aemtask.core.property.listeners;

import com.aemtask.core.property.config.PropertyRemovalListenerConfig;
import com.aemtask.core.property.utils.ListenerUtils;
import com.aemtask.core.property.utils.PathNameUtils;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Property removal tracker event listener
 * Tracking only properties removing events
 */
@Component(
        service = EventListener.class,
        immediate = true
)
public class PropertyRemovalEventListener implements EventListener {

    private static final String INFO_MSG_EVENT_PROCESSING_START = "Event processing start (iterator = %s, size = %s)";
    private static final String INFO_MSG_EVENT_LISTENER_REGISTERED =
            "Content changes listener has been registered (types = %s, path = %s, isDeep = %s, uuids = %s, node types = %s, noLocal = %s";
    private static final String INFO_MSG_START_PROCESSING_SUB_EVENT = "Start processing sub event (path = %s,  type = %s, info = %s";
    private static final String ERROR_MSG_LISTENER_REGISTRATION = "Unable to register listener";
    private static final String ERROR_MSG_DEACTIVATING_EVENT_LISTENER = "Error deactivating of event listener";
    private static final String ERROR_MSG_TREATING_EVENTS = "Error while treating events";

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private Session observationSession;

    @Reference
    private ListenerUtils listenerUtils;

    @Reference
    private PathNameUtils pathNameUtils;

    @Reference
    private PropertyRemovalListenerConfig listenerConfig;

    @Reference
    private SlingRepository repository;

    @Reference
    private JobManager jobManager;

    @Override
    public void onEvent(EventIterator eventIterator) {
        LOGGER.info(String.format(INFO_MSG_EVENT_PROCESSING_START, eventIterator.toString(), eventIterator.getSize()));
        while (eventIterator.hasNext()) {
            try {
                Event event = eventIterator.nextEvent();
                LOGGER.info(String.format(INFO_MSG_START_PROCESSING_SUB_EVENT, event.getPath(), event.getType(), event.getInfo()));
                startJob(event.getPath(), pathNameUtils.getPropertyNameFromPath(event.getPath()));
            } catch (RepositoryException e) {
                LOGGER.error(ERROR_MSG_TREATING_EVENTS, e);
            }
        }
    }

    @Activate
    public void activate() {
        try {
            observationSession = repository.login(listenerConfig.getCredentials());
            observationSession.getWorkspace()
                    .getObservationManager()
                    .addEventListener(
                            this,
                            listenerUtils.getEventTypes(),
                            listenerUtils.getPath(),
                            listenerUtils.getIsDeep(),
                            listenerUtils.getUuidArr(),
                            listenerUtils.getNodeTypes(),
                            listenerUtils.getNoLocal());
            LOGGER.info(String.format(INFO_MSG_EVENT_LISTENER_REGISTERED,
                    listenerUtils.getEventTypes(),
                    listenerUtils.getPath(),
                    listenerUtils.getIsDeep(),
                    Arrays.toString(listenerUtils.getUuidArr()),
                    Arrays.toString(listenerUtils.getNodeTypes()),
                    listenerUtils.getNoLocal()));
        } catch (RepositoryException e) {
            LOGGER.error(ERROR_MSG_LISTENER_REGISTRATION, e);
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

    private void startJob(String propertyPath, String propertyName) {
        final Map<String, Object> props = new HashMap<>();
        props.put(pathNameUtils.getPropertyPathKey(), propertyPath);
        props.put(pathNameUtils.getPropertyNameKey(), propertyName);
        jobManager.createJob(ListenerUtils.PROPERTY_REMOVAL_JOB_NAME).properties(props).add();
    }


}
