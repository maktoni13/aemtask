package com.aemtask.core.search.service;

import javax.jcr.Node;
import java.util.List;

/**
 * Text search provider service
 */
public interface TextSearchProvider {
    String NODE_TYPE = "dam:Asset";
    String READ_SERVICE = "readService";

    boolean implementsApi(String searchApiName);
    List<Node> getResult(String[] paths, String text);
}
