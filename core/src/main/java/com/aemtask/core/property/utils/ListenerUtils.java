package com.aemtask.core.property.utils;

/**
 * Property removal tracker properties utils component interface
 */
public interface ListenerUtils {

    String PROPERTY_REMOVAL_JOB_NAME = "aemtask/property/removal";
    String REMOVED_PROPERTIES_STORE_PATH = "/var/log/removedProperties";
    String REMOVED_PROPERTIES_PATH_HINT = "/var/log/removedProperties/prop";

    int getEventTypes();
    boolean getNoLocal();
    boolean getIsDeep();
    String getPath();
    String[] getNodeTypes();
    String[] getUuidArr();
}