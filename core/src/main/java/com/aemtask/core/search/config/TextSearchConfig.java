package com.aemtask.core.search.config;

import java.util.Map;

/**
 * Text search configuration service component
 */
public interface TextSearchConfig {
    String getText();
    String[] getPaths();
    String getSearchAPI();
    Map<String, Object> getAuthParams();
}
