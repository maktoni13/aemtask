package com.aemtask.core.search.config;

/**
 * Text search configuration service component
 */
public interface TextSearchConfig {
    String getText();
    String[] getPaths();
    String getSearchAPI();
}
