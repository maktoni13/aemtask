package com.aemtask.core.workflow.listenerstart.utils;

/**
 * Page adding workflow properties utils component interface
 */
public interface PageAddingWorkflowUtils {
    int getEventTypes();
    boolean getNoLocal();
    boolean getIsDeep();
    String getPath();
    String[] getNodeTypes();
    String[] getUuidArr();
    String getWorkflowName();
}