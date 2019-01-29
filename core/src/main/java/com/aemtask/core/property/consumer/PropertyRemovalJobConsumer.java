package com.aemtask.core.property.consumer;

import com.aemtask.core.property.config.PropertyRemovalListenerConfig;
import com.aemtask.core.property.utils.ListenerUtils;
import com.aemtask.core.property.utils.PathNameUtils;
import com.day.cq.commons.jcr.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Property removal tracker job consumer
 */
@Component(
        service = JobConsumer.class,
        immediate = true,
        property = {
                JobConsumer.PROPERTY_TOPICS + "=" + ListenerUtils.PROPERTY_REMOVAL_JOB_NAME
        }
)
public class PropertyRemovalJobConsumer implements JobConsumer {

    private static final String INFO_MSG_NODE_CREATED = "Removed property node created (path = %s)";
    private static final String ERROR_MSG_NODE_CREATION_FAILED = "Removed property node creation failed";

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());


    @Reference
    private ListenerUtils listenerUtils;

    @Reference
    private PropertyRemovalListenerConfig listenerConfig;

    @Reference
    private SlingRepository repository;

    @Reference
    private PathNameUtils pathNameUtils;

    @Override
    public JobResult process(Job job) {

        try {
            Session session = repository.login(listenerConfig.getCredentials());
            JcrUtils.getOrCreateByPath(ListenerUtils.REMOVED_PROPERTIES_STORE_PATH,
                    JcrResourceConstants.NT_SLING_FOLDER,
                    session);
            Node node = JcrUtils.getOrCreateUniqueByPath(
                    ListenerUtils.REMOVED_PROPERTIES_PATH_HINT,
                    JcrConstants.NT_UNSTRUCTURED,
                    session);
            setProperty(node, pathNameUtils.getPropertyNameKey(), job);
            setProperty(node, pathNameUtils.getPropertyPathKey(), job);
            LOGGER.info(String.format(INFO_MSG_NODE_CREATED, node.getPath()));
            session.save();
            session.logout();
        } catch (RepositoryException e) {
            LOGGER.error(ERROR_MSG_NODE_CREATION_FAILED, e);
            return JobResult.FAILED;
        }

        return JobResult.OK;
    }

    private void setProperty(Node node, String property, Job job) throws RepositoryException {
        node.setProperty(property, (String) job.getProperty(property));
    }
}
