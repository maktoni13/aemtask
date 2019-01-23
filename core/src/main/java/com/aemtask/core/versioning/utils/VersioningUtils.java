package com.aemtask.core.versioning.utils;

/**
 * Versioning properties utils component interface
 */
public interface VersioningUtils {
    int getEventTypes();
    boolean getNoLocal();
    boolean getIsDeep();
    String getPath();
    String[] getNodeTypes();
    String[] getUuidArr();
}