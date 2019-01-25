package com.aemtask.core.property.config.impl;

import com.aemtask.core.property.config.PropertyRemovalListenerConfig;
import lombok.Getter;
import lombok.Setter;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.SimpleCredentials;

/**
 * Property removal tracker configuration service component implementation
 */
@Component(
        service = {PropertyRemovalListenerConfig.class},
        immediate = true
)
@Designate(ocd = PropertyRemovalListenerConfigImpl.Config.class)
@Setter
public class PropertyRemovalListenerConfigImpl implements PropertyRemovalListenerConfig {

    @ObjectClassDefinition(name = "Versioning of content changes configuration")
    public static @interface Config {

        @AttributeDefinition(name = "username")
        String username();

        @AttributeDefinition(name = "password", type = AttributeType.PASSWORD)
        String password();

    }

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private String user;
    private String password;

    @Getter
    private SimpleCredentials credentials;

    @Activate
    @Modified
    public void activate(final Config config) {
        setUser(config.username());
        setPassword(config.password());
        setCredentials(new SimpleCredentials(config.username(), config.password().toCharArray()));
    }
}
