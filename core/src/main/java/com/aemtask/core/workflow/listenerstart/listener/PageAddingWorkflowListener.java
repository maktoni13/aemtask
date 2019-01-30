package com.aemtask.core.workflow.listenerstart.listener;

import com.adobe.granite.workflow.payload.PayloadInfo;
import com.aemtask.core.workflow.config.PageAddingWorkflowConfig;
import com.aemtask.core.workflow.listenerstart.utils.PageAddingWorkflowUtils;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.Workflow;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;
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
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;
import java.util.Arrays;


/**
 * Page adding creation workflow listener
 */
@Component(
        service = EventListener.class,
        immediate = true
)
public class PageAddingWorkflowListener implements EventListener{

    private static final String INFO_MSG_EVENT_PROCESSING_START = "Event processing start (iterator = %s, size = %s)";
    private static final String INFO_MSG_EVENT_LISTENER_REGISTERED =
            "Content changes listener has been registered (types = %s, path = %s, isDeep = %s, uuids = %s, node types = %s, noLocal = %s";
    private static final String INFO_MSG_START_PROCESSING_SUB_EVENT = "Start processing sub event (path = %s,  type = %s, info = %s";
    private static final String INFO_MSG_CREATION_BY_LISTENER_IS_OFF = "Page review workflow creation by Listener is off";
    private static final String INFO_MSG_REVIEW_WORKFLOW_STARTED = "Page adding review workflow started";
    private static final String ERROR_MSG_DEACTIVATING_EVENT_LISTENER = "Error deactivating of event listener";
    private static final String ERROR_MSG_TREATING_EVENTS = "Error while treating events";
    private static final String ERROR_MSG_SESSION_REGISTRATION = "Unable to register session";

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private Session observationSession;

    @Reference
    private PageAddingWorkflowConfig pageAddingWorkflowConfig;

    @Reference
    private PageAddingWorkflowUtils utils;

    @Reference
    private SlingRepository repository;

    @Reference
    private WorkflowService workflowService;

    private void startWorkflow(Event event) throws WorkflowException, RepositoryException {
        Session session = repository.login(pageAddingWorkflowConfig.getCredentials());
        Node node = session.getNodeByIdentifier(event.getIdentifier());
        WorkflowSession wfSession = workflowService.getWorkflowSession(session);
        WorkflowModel wfModel = wfSession.getModel(utils.getWorkflowName());
        WorkflowData wfData = wfSession.newWorkflowData(PayloadInfo.PAYLOAD_TYPE.JCR_PATH.name(), node.getPath());
        Workflow wf = wfSession.startWorkflow(wfModel, wfData);
        LOGGER.info(INFO_MSG_REVIEW_WORKFLOW_STARTED, wf);
    }

    @Override
    public void onEvent(EventIterator eventIterator) {
        if (!pageAddingWorkflowConfig.isCreateWithListener()){
            LOGGER.info(INFO_MSG_CREATION_BY_LISTENER_IS_OFF);
            return;
        }
        LOGGER.info(String.format(INFO_MSG_EVENT_PROCESSING_START, eventIterator.toString(), eventIterator.getSize()));
        while (eventIterator.hasNext()) {
            try {
                Event event = eventIterator.nextEvent();
                LOGGER.info(String.format(INFO_MSG_START_PROCESSING_SUB_EVENT, event.getPath(), event.getType(), event.getInfo()));
                startWorkflow(event);
            } catch (RepositoryException | WorkflowException e) {
                LOGGER.error(ERROR_MSG_TREATING_EVENTS, e);
            }
        }
    }

    @Activate
    public void activate() {
        try {
            observationSession = repository.login(pageAddingWorkflowConfig.getCredentials());
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
