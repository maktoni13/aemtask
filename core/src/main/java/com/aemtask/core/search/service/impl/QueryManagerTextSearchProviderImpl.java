package com.aemtask.core.search.service.impl;

import com.aemtask.core.search.service.TextSearchProvider;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Text search provider implementation with Query Manager and JCR.SQL2
 */
@Component(
        service = {TextSearchProvider.class},
        immediate = true
)
public class QueryManagerTextSearchProviderImpl implements TextSearchProvider {

    private static final String QUERY_MANAGER_ERROR_MSG = "Query manager creation error: ";
    private static final String QUERY_MANAGER_API = "QueryManager";

    private final Logger LOGGER = LoggerFactory.getLogger(QueryManagerTextSearchProviderImpl.class);

    @Reference
    private ResourceResolverFactory resolverFactory;

    private String prepareSqlStmt(String[] paths, String text) {
        return "SELECT * " +
                "FROM [" + NODE_TYPE + "] AS s " +
                "WHERE " + prepareNodeStmt(paths) +
                "CONTAINS(s.*, '" + text + "')";
    }

    private String prepareNodeStmt(String[] paths) {
        String result = "";
        if (paths.length > 0) {
            result += "(";
            for (String path : paths) {
                result += ("(".equals(result)) ? "" : " OR ";
                result += "ISDESCENDANTNODE([" + path + "])";
            }
            result += ") " +
                    "AND ";
        }
        return result;
    }

    @Override
    public boolean implementsApi(String searchApiName) {
        return QUERY_MANAGER_API.equals(searchApiName);
    }

    @Override
    public List<Node> getResult(String[] paths, String text) {
        String sqlStatement = prepareSqlStmt(paths, text);
        List<Node> nodeList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, READ_SERVICE);
        param.put(ResourceResolverFactory.USER, "admin");
        param.put(ResourceResolverFactory.PASSWORD, "admin".toCharArray());

        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = resolverFactory.getResourceResolver(param);
            Session session = resourceResolver.adaptTo(Session.class);
            QueryManager queryManager = session.getWorkspace().getQueryManager();
            Query query = queryManager.createQuery(sqlStatement, Query.JCR_SQL2);
            QueryResult queryResult = query.execute();
            NodeIterator nodeIterator = queryResult.getNodes();
            while (nodeIterator.hasNext()) {
                nodeList.add(nodeIterator.nextNode());
            }
            return nodeList;
        } catch (RepositoryException | LoginException e) {
            LOGGER.error(QUERY_MANAGER_ERROR_MSG, e);
        }
        return nodeList;
    }
}
