package com.aemtask.core.workflow.config;

import javax.jcr.SimpleCredentials;

/**
 * Page adding workflow of content changes configuration service component interface
 */
public interface PageAddingWorkflowConfig {
    SimpleCredentials getCredentials();
    boolean isCreateWithListener();
}
