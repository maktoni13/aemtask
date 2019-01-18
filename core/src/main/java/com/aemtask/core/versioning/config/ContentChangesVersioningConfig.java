package com.aemtask.core.versioning.config;

import javax.jcr.SimpleCredentials;

/**
 * Versioning of content changes configuration service component interface
 */
public interface ContentChangesVersioningConfig {
    SimpleCredentials getCredentials();
    boolean isAutoVersioning();
}
