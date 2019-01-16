package com.aemtask.core.search.service.impl;

import com.aemtask.core.search.config.TextSearchConfig;
import com.aemtask.core.search.service.TextSearchProvider;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Text search provider implementation with Query Builder
 */
@Component(
        service = {TextSearchProvider.class},
        immediate = true
)
public class QueryBuilderTextSearchProviderImpl implements TextSearchProvider {

    private static final String QUERY_BUILDER_ERROR_MSG = "Query builder creation error: ";
    private static final String QUERY_BUILDER_API = "QueryBuilder";
    private static final String GROUP_PATH_KEY_PREDICATE_TEMPLATE = "group.%s_path";
    private static final String GROUP_OR_FLAG_KEY = "group.p.or";
    private static final String FULLTEXT_KEY = "fulltext";
    private static final String TYPE_KEY = "type";

    private final Logger LOGGER = LoggerFactory.getLogger(QueryBuilderTextSearchProviderImpl.class);

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private TextSearchConfig textSearchConfig;

    private PredicateGroup preparePredicateGroup(String text, String[] paths) {
        Map<String, String> predicateMap = new HashMap<>();
        predicateMap.put(TYPE_KEY, NODE_TYPE);
        predicateMap.put(FULLTEXT_KEY, text);
        predicateMap.put(GROUP_OR_FLAG_KEY, Boolean.TRUE.toString());
        for (int i = 0; i < paths.length; i++) {
            predicateMap.put(String.format(GROUP_PATH_KEY_PREDICATE_TEMPLATE, i + 1), paths[i]);
        }
        return PredicateGroup.create(predicateMap);
    }

    @Override
    public boolean implementsApi(String searchApiName) {
        return QUERY_BUILDER_API.equals(searchApiName);
    }

    @Override
    public List<Node> getResult(String[] paths, String text) {
        ResourceResolver resourceResolver = null;
        QueryBuilder queryBuilder = null;
        List<Node> nodeList = new ArrayList<>();

        try {
            resourceResolver = resolverFactory.getResourceResolver(textSearchConfig.getAuthParams());
            queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);

        } catch (LoginException e) {
            LOGGER.error(QUERY_BUILDER_ERROR_MSG, e);
        }

        if (queryBuilder != null) {
            Query query = queryBuilder.createQuery(
                    preparePredicateGroup(text, paths),
                    resourceResolver.adaptTo(Session.class));
            SearchResult result = query.getResult();
            Iterator<Node> nodeIterator = result.getNodes();
            while (nodeIterator.hasNext()) {
                nodeList.add(nodeIterator.next());
            }
        }

        return nodeList;
    }
}
