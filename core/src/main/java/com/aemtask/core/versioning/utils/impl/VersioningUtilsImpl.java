package com.aemtask.core.versioning.utils.impl;

import com.aemtask.core.versioning.utils.VersioningUtils;
import org.osgi.service.component.annotations.Component;

import javax.jcr.observation.Event;

/**
 * Versioning properties utils component implementation
 */
@Component(
        service = {VersioningUtils.class},
        immediate = true
)
public class VersioningUtilsImpl implements VersioningUtils {

    private final int EVENT_TYPES = Event.NODE_ADDED
            | Event.PROPERTY_CHANGED
            | Event.PROPERTY_ADDED
            | Event.PROPERTY_REMOVED;
    private final boolean NO_LOCAL = true;
    private final boolean IS_DEEP = true;
    private final String PATH = "/content/aemtask";
    private final String[] NODE_TYPES = null;
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

}
