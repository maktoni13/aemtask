package com.aemtask.core.workflow.utils.impl;

import com.adobe.acs.commons.util.ResourceUtil;
import com.adobe.aemds.guide.utils.JcrResourceConstants;
import com.aemtask.core.workflow.utils.PathValidationUtils;
import com.day.cq.commons.jcr.JcrUtil;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Page adding workflow path validation utils component implementation
 */
@Component(
        service = {PathValidationUtils.class},
        immediate = true
)
public class PathValidationUtilsImpl implements PathValidationUtils {

    private static final String ERR_MSG_PATH_IS_EMPTY = "Paths validation. Path is empty (source = %s, destination = %s)";
    private static final String ERR_MSG_ILLEGAL_CHARACTERS = "Paths validation. Illegal characters in full dest path (source = %s, destination = %s)";
    private static final String ERR_MSG_DESTINATION_UNDER_SOURCE = "Paths validation. Destination under source (source = %s, destination = %s)";
    private static final String ERR_MSG_EQUAL_PATHS = "Paths validation. Equal paths (source = %s, destination = %s)";
    private static final String NO_DESTINATION_PATH = "Paths validation. There is no destination path (source = %s, destination = %s)";
    private static final String ERR_MSG_DEST_NODE_EXISTS = "Paths validation. Destination node already exists (source = %s, destination = %s)";

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Reference
    SlingRepository slingRepository;


    @Override
    public boolean isPathsValid(String sourcePath, String destinationPath, Session session) throws RepositoryException {
        if (destinationPath == null
                || sourcePath == null
                || destinationPath.isEmpty()){
            LOGGER.error(String.format(ERR_MSG_PATH_IS_EMPTY, sourcePath, destinationPath));
            return false;
        }
        String fullDestPath = getFullDestPath(sourcePath, destinationPath);
        if(JcrUtil.isValidName(fullDestPath)){
            LOGGER.error(String.format(ERR_MSG_ILLEGAL_CHARACTERS, sourcePath, destinationPath));
            return false;
        }
        if (destinationPath.startsWith(sourcePath)){
            LOGGER.error(String.format(ERR_MSG_DESTINATION_UNDER_SOURCE, sourcePath, destinationPath));
            return false;
        }
        if(fullDestPath.equals(sourcePath)){
            LOGGER.error(String.format(ERR_MSG_EQUAL_PATHS, sourcePath, destinationPath));
            return false;
        }
        if(session.nodeExists(fullDestPath)){
            LOGGER.error(String.format(ERR_MSG_DEST_NODE_EXISTS, sourcePath, destinationPath));
            return false;
        }

        if(!session.itemExists(destinationPath)){
            LOGGER.error(String.format(NO_DESTINATION_PATH, sourcePath, destinationPath));
            return false;
        }

        return true;

    }

    @Override
    public String getFullDestPath(String sourcePath, String destinationPath) {
        return destinationPath + sourcePath.substring(sourcePath.lastIndexOf("/"));
    }
}
