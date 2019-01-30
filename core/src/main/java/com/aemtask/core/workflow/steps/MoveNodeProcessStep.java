package com.aemtask.core.workflow.steps;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.aemtask.core.workflow.utils.PathValidationUtils;
import com.day.cq.commons.jcr.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;


/**
 * Move Node Step of page review workflow
 */
@Component(
        service = WorkflowProcess.class,
        immediate = true,
        property = {
                "process.label=AEM Task: Move Node Step"
        }
)
public class MoveNodeProcessStep implements WorkflowProcess {

    private static final String ERR_MSG_INCORRECT_VALUES_IN_PATHS = "Payload not moved because of incorrect values in paths (source = %s, destination = %s)";
    private static final String ERR_MSG_CANT_RESOLVE_SESSION = "Can't resolve jcr session from workflow session (session = %s)";
    private static final String ERR_MSG_CANT_FOUND_PAYLOAD_NODE = "Can't found payload node (path = %s)";
    private static final String ERR_MSG_CANT_FOUND_PATH_TO_MOVE_PROP = "Can't found %s property in jcr content of payload node (path = %s)";
    private static final String ERR_MSG_EXECUTING_MOVE_NODE_STEP = "Error while executing Move Node Step of Page review workflow (source = %s)";

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Reference
    private PathValidationUtils pathValidationUtils;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        String sourcePath = workItem.getWorkflowData().getPayload().toString();
        Session session = workflowSession.adaptTo(Session.class);
        if (session == null){
            LOGGER.error(String.format(ERR_MSG_CANT_RESOLVE_SESSION, workflowSession.toString()));
            return;
        }
        try {
            Node node = JcrUtils.getNodeIfExists(sourcePath, session);
            if (node == null){
                LOGGER.error(String.format(ERR_MSG_CANT_FOUND_PAYLOAD_NODE, sourcePath));
                return;
            }
            Node contentNode = node.getNode(JcrConstants.JCR_CONTENT);
            if (!contentNode.hasProperty(PathValidationUtils.PATH_TO_MOVE_PROP)){
                LOGGER.error(String.format(ERR_MSG_CANT_FOUND_PATH_TO_MOVE_PROP,
                        PathValidationUtils.PATH_TO_MOVE_PROP,
                        sourcePath));
                return;
            }
            String destinationPath = contentNode.getProperty(PathValidationUtils.PATH_TO_MOVE_PROP).getString();
            if (!pathValidationUtils.isPathsValid(sourcePath, destinationPath, session)){
                LOGGER.error(String.format(ERR_MSG_INCORRECT_VALUES_IN_PATHS, sourcePath, destinationPath));
            } else {
                session.move(sourcePath, pathValidationUtils.getFullDestPath(sourcePath, destinationPath));
                session.save();
            }
        } catch (RepositoryException e) {
            LOGGER.error(String.format(ERR_MSG_EXECUTING_MOVE_NODE_STEP, sourcePath), e);
        }
    }
}
