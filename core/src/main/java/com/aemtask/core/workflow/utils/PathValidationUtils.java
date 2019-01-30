package com.aemtask.core.workflow.utils;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Page adding workflow path validation utils component interface
 */
public interface PathValidationUtils {
    String PATH_TO_MOVE_PROP = "pathToMove";
    boolean isPathsValid(String sourcePath, String destinationPath, Session session) throws RepositoryException;
    String getFullDestPath(String sourcePath, String destinationPath);
}
