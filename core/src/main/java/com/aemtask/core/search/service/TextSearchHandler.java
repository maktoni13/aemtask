package com.aemtask.core.search.service;

import javax.jcr.Node;
import java.util.List;

/**
 * Text search handler for multi implementation usage
 */
public interface TextSearchHandler {
    List<Node> getResult();
    List<Node> getResult(String text, String[] paths, String searchApi);
}
