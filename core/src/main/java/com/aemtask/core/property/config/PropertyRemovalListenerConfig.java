package com.aemtask.core.property.config;

import javax.jcr.SimpleCredentials;

/**
 * Property removal tracker configuration service component interface
 */
public interface PropertyRemovalListenerConfig {
    SimpleCredentials getCredentials();
}
