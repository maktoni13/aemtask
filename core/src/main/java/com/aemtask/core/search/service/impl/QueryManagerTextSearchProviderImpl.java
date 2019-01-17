package com.aemtask.core.search.service.impl;

import com.aemtask.core.search.config.TextSearchConfig;
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
import javax.jcr.Workspace;
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
    private static final String SESSION_LOGIN_ERROR_MSG = "Cannot create session: ";
    private static final String QUERY_MANAGER_API = "QueryManager";
    private static final String SQL2_QUERY_TEMPLATE = "SELECT * FROM [%s] AS s WHERE %s CONTAINS(s.*, '%s')";
    private static final String SQL2_PATH_CONDITION_TEMPLATE = "ISDESCENDANTNODE([%s])";

    private final Logger LOGGER = LoggerFactory.getLogger(QueryManagerTextSearchProviderImpl.class);

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private TextSearchConfig textSearchConfig;

    private String prepareSqlStmt(String[] paths, String text) {
        return String.format(SQL2_QUERY_TEMPLATE, NODE_TYPE, prepareNodeStmt(paths), text);
    }

    private String prepareNodeStmt(String[] paths) {
        StringBuilder stringBuilder = new StringBuilder();
        if (paths.length > 0) {
            stringBuilder.append("(");
            for (String path : paths) {
                stringBuilder.append((stringBuilder.length() > 1) ? " OR " : "");
                stringBuilder.append(String.format(SQL2_PATH_CONDITION_TEMPLATE, path));
            }
            stringBuilder.append(") AND ");
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean implementsApi(String searchApiName) {
        return QUERY_MANAGER_API.equals(searchApiName);
    }

    @Override
    public List<Node> getResult(String[] paths, String text) {

        String sqlStatement = prepareSqlStmt(paths, text);
        List<Node> nodeList = new ArrayList<>();
        Session session = null;
        ResourceResolver resourceResolver = null;

        try {
            resourceResolver = resolverFactory.getResourceResolver(textSearchConfig.getAuthParams());
            session = resourceResolver.adaptTo(Session.class);
        } catch (LoginException e) {
            LOGGER.error(SESSION_LOGIN_ERROR_MSG, e);
        }

        if (session != null) {
            try {
                QueryManager queryManager = session.getWorkspace().getQueryManager();
                Query query = queryManager.createQuery(sqlStatement, Query.JCR_SQL2);
                QueryResult queryResult = query.execute();
                NodeIterator nodeIterator = queryResult.getNodes();
                while (nodeIterator.hasNext()) {
                    nodeList.add(nodeIterator.nextNode());
                }
            } catch (RepositoryException e) {
                LOGGER.error(QUERY_MANAGER_ERROR_MSG, e);
            }
        }

        return nodeList;
    }
}
