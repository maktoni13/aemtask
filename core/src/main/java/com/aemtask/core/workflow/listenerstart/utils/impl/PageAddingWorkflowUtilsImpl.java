package com.aemtask.core.workflow.listenerstart.utils.impl;

import com.aemtask.core.workflow.listenerstart.utils.PageAddingWorkflowUtils;
import org.osgi.service.component.annotations.Component;

import javax.jcr.observation.Event;

/**
 * Page adding workflow properties utils component implementation
 */
@Component(
        service = {PageAddingWorkflowUtils.class},
        immediate = true
)
public class PageAddingWorkflowUtilsImpl implements PageAddingWorkflowUtils {

    private static final String VAR_WORKFLOW_MODELS_AEMTASK_WORKFLOW = "/var/workflow/models/aemtask-workflow";
    private static final int EVENT_TYPES = Event.NODE_ADDED;
    private static final boolean NO_LOCAL = true;
    private static final boolean IS_DEEP = false;
    private static final String PATH = "/content/we-retail";

    private final String[] NODE_TYPES = {"cq:Page"};
    private final String[] UUID_ARR = null;

    @Override
    public int getEventTypes() {
        return EVENT_TYPES;
    }

    @Override
    public boolean getNoLocal() {
        return NO_LOCAL;
    }

    @Override
    public boolean getIsDeep() {
        return IS_DEEP;
    }

    @Override
    public String getPath() {
        return PATH;
    }

    @Override
    public String[] getNodeTypes() {
        return NODE_TYPES;
    }

    @Override
    public String[] getUuidArr(){
        return UUID_ARR;
    }

    @Override
    public String getWorkflowName() {
        return VAR_WORKFLOW_MODELS_AEMTASK_WORKFLOW;
    }
}
