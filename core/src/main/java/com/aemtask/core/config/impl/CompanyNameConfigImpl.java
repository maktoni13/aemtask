package com.aemtask.core.config.impl;

import com.aemtask.core.config.CompanyNameConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Company name configuration service component implementation for replace filter
 */
@Component(
        service = { CompanyNameConfig.class },
        immediate = true
)
@Designate(ocd = CompanyNameConfigImpl.Config.class)
public class CompanyNameConfigImpl implements CompanyNameConfig {

    @ObjectClassDefinition(name = "Company name configuration")
    public static @interface Config {
        @AttributeDefinition(name = "source")
        String source() default "";
        @AttributeDefinition(name = "replacement")
        String replacement() default "";
    }

    private static final String LOGGER_CONF_SOURCE_MSG = "configure: source='{}''";
    private static final String LOGGER_CONF_REPLACEMENT_MSG = "configure: source='{}''";

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private String source;
    private String replacement;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    @Activate
    protected void activate(final Config config) {
        setSource(config.source());
        LOGGER.info(LOGGER_CONF_SOURCE_MSG, getSource());
        setReplacement(config.replacement());
        LOGGER.info(LOGGER_CONF_REPLACEMENT_MSG, getReplacement());
    }


}
