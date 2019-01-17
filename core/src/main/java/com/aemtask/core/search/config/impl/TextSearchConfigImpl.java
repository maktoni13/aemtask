package com.aemtask.core.search.config.impl;

import com.aemtask.core.search.config.TextSearchConfig;
import lombok.Setter;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

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

    private static final String READ_SERVICE = "readService";

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

        @AttributeDefinition(name = "username")
        String username() default "";

        @AttributeDefinition(name = "password", type = AttributeType.PASSWORD)
        String password() default "";
    }

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private String text;
    private String[] paths;
    private String searchApi;
    private String user;
    private String password;
    private Map<String, Object> authParams;

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

    @Override
    public Map<String, Object> getAuthParams() {
        return authParams;
    }

    @Activate
    @Modified
    public void activate(final Config config) {
        setText(config.text());
        setPaths(config.paths());
        setSearchApi(config.searchApi());
        setUser(config.username());
        setPassword(config.password());
        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, READ_SERVICE);
        param.put(ResourceResolverFactory.USER, config.username());
        param.put(ResourceResolverFactory.PASSWORD, config.password().toCharArray());
        setAuthParams(param);
    }
}
