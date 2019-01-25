package com.aemtask.core.property.utils.impl;

import com.aemtask.core.property.utils.PathNameUtils;
import org.osgi.service.component.annotations.Component;

/**
 * Property removal tracker path/name utils component implementation
 */
@Component(
        service = {PathNameUtils.class},
        immediate = true
)
public class PathNameUtilsImpl implements PathNameUtils {

    private static final String PROPERTY_NAME_KEY = "propertyName";
    private static final String PROPERTY_PATH_KEY = "propertyPath";

    @Override
    public String getPropertyNameFromPath(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    @Override
    public String getPropertyNameKey() {
        return PROPERTY_NAME_KEY;
    }

    @Override
    public String getPropertyPathKey() {
        return PROPERTY_PATH_KEY;
    }
}
