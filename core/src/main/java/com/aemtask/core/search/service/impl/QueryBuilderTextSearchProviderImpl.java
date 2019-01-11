package com.aemtask.core.search.service.impl;

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
        service = { TextSearchProvider.class },
        immediate = true
)
public class QueryBuilderTextSearchProviderImpl implements TextSearchProvider {

    private static final String QUERY_BUILDER_ERROR_MSG = "Query builder creation error: ";
    private static final String QUERY_BUILDER_API = "QueryBuilder";

    private final Logger LOGGER = LoggerFactory.getLogger(QueryBuilderTextSearchProviderImpl.class);

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Override
    public boolean implementsApi(String searchApiName) {
        return QUERY_BUILDER_API.equals(searchApiName);
    }

    @Override
    public List<Node> getResult(String[] paths, String text) {
        ResourceResolver resourceResolver = null;

        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, READ_SERVICE);
        param.put(ResourceResolverFactory.USER, "admin");
        param.put(ResourceResolverFactory.PASSWORD, "admin".toCharArray());
        try {
            resourceResolver = resolverFactory.getResourceResolver(param);
            Map<String, String> predicateMap = new HashMap<>();
            predicateMap.put("type", NODE_TYPE);
            predicateMap.put("fulltext", text);
            predicateMap.put("group.p.or", "true");
            for (int i = 1; i <= paths.length; i++) {
                predicateMap.put("group." + i + "path", paths[i - 1]);
            }
            QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
            Query query = queryBuilder.createQuery(
                    PredicateGroup.create(predicateMap),
                    resourceResolver.adaptTo(Session.class));
            SearchResult result = query.getResult();
            List<Node> nodes = new ArrayList<>();
            Iterator<Node> nodeIterator = result.getNodes();
            while (nodeIterator.hasNext()) {
                nodes.add(nodeIterator.next());
            }
            return nodes;

        } catch (LoginException e) {
            LOGGER.error(QUERY_BUILDER_ERROR_MSG, e);
        }
        return null;
    }
}
