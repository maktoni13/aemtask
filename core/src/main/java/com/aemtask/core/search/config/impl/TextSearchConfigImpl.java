package com.aemtask.core.search.config.impl;

import com.aemtask.core.search.config.TextSearchConfig;
import lombok.Setter;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Text search configuration service component implementation
 */
@Component(
        service = { TextSearchConfig.class },
        immediate = true
)
@Designate(ocd = TextSearchConfigImpl.Config.class)
@Setter
public class TextSearchConfigImpl implements TextSearchConfig {

    @ObjectClassDefinition(name = "Text search configuration")
    public static @interface Config {

        @AttributeDefinition(name = "text", options = {
                @Option(label = "industry leadership", value = "industry leadership"),
                @Option(label = "successful product", value = "successful product")
        })
        String text() default "";

        @AttributeDefinition(name = "paths", options = {
                @Option(label = "/content/dam/aemtask", value = "/content/dam/aemtask"),
                @Option(label = "/content/dam/we-retail", value = "/content/dam/we-retail")
        })
        String[] paths() default "";

        @AttributeDefinition(name = "searchApi", defaultValue = "QueryManager", options = {
                @Option(label = "Query builder", value = "QueryBuilder"),
                @Option(label = "Query manager", value = "QueryManager")
        })
        String searchApi() default "";

    }

    private static final String LOGGER_CONF_TEXT_MSG = "configure: text='{}''";
    private static final String LOGGER_CONF_PATHS_MSG = "configure: paths='{}''";
    private static final String LOGGER_CONF_SEARCH_API_MSG = "configure: searchApi='{}''";

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private String text;
    private String[] paths;
    private String searchApi;

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String[] getPaths() {
        return paths == null ? new String[0] : paths;
    }

    @Override
    public String getSearchAPI() {
        return searchApi;
    }

    @Activate
    @Modified
    public void activate(final Config config) {
        setText(config.text());
        LOGGER.info(LOGGER_CONF_TEXT_MSG, getText());
        setPaths(config.paths());
        LOGGER.info(LOGGER_CONF_PATHS_MSG, getPaths());
        setSearchApi(config.searchApi());
        LOGGER.info(LOGGER_CONF_SEARCH_API_MSG, getSearchAPI());
    }

}
