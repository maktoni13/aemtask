package com.aemtask.core.versioning.utils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Node utils component interface for searching root node under given path and criteria validation
 */
public interface NodeUtils {
    String ERROR_MSG_SEARCHING_ROOT_NODE = "Root node searching error (node = %s, path = %s";
    Node getRootPageNode(Node node, String path);
    Node getParentNode(Node node, String path) throws RepositoryException;
    boolean isDescriptionPresent(Node node) throws RepositoryException;
}
