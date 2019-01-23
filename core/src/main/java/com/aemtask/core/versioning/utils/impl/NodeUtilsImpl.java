package com.aemtask.core.versioning.utils.impl;

import com.aemtask.core.versioning.utils.NodeUtils;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.NameConstants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Node utils component for searching root node under given path and criteria validation implementation
 */
@Component(
        service = {NodeUtils.class},
        immediate = true
)
public class NodeUtilsImpl implements NodeUtils {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public Node getRootPageNode(Node node, String path) {
        try {
            return getParentNode(node, path);
        } catch (RepositoryException e) {
            LOGGER.error(String.format(ERROR_MSG_SEARCHING_ROOT_NODE, node.toString(), path), e);
        }
        return null;
    }

    @Override
    public Node getParentNode(Node node, String path) throws RepositoryException {
        if (node == null
                || node.getParent() == null
                || path.equals(node.getPath())) {
            return null;
        } else if (node.isNodeType(NameConstants.NT_PAGE)
                && path.equals(node.getParent().getPath())) {
            return node;
        }
        return getParentNode(node.getParent(), path);
    }

    @Override
    public boolean isDescriptionPresent(Node node) throws RepositoryException{
        if (node.hasNode(JcrConstants.JCR_CONTENT)){
            Node contentNode = node.getNode(JcrConstants.JCR_CONTENT);
            return contentNode.hasProperty(JcrConstants.JCR_DESCRIPTION)
                    && !contentNode.getProperty(JcrConstants.JCR_DESCRIPTION).getValue().getString().isEmpty();
        }
        return false;
    }
}
