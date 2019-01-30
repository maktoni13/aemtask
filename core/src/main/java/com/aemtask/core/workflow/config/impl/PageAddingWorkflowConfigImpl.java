package com.aemtask.core.workflow.config.impl;

import com.aemtask.core.workflow.config.PageAddingWorkflowConfig;
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
 * Page adding workflow of content changes configuration service component implementation
 */
@Component(
        service = { PageAddingWorkflowConfig.class },
        immediate = true
)
@Designate(ocd = PageAddingWorkflowConfigImpl.Config.class)
@Setter
public class PageAddingWorkflowConfigImpl implements PageAddingWorkflowConfig {

    @ObjectClassDefinition(name = "Versioning of content changes configuration")
    public static @interface Config {

        @AttributeDefinition(name = "username")
        String username();

        @AttributeDefinition(name = "password", type = AttributeType.PASSWORD)
        String password();

        @AttributeDefinition(name = "createWithListener")
        boolean createWithListener() default false;

    }

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private String user;
    private String password;

    @Getter
    private SimpleCredentials credentials;

    @Getter
    private boolean createWithListener;

    @Activate
    @Modified
    public void activate(final Config config) {
        setUser(config.username());
        setPassword(config.password());
        setCredentials(new SimpleCredentials(config.username(), config.password().toCharArray()));
        setCreateWithListener(config.createWithListener());
    }
}
