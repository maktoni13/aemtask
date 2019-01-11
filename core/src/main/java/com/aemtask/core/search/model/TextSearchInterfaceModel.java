package com.aemtask.core.search.model;

import com.aemtask.core.search.service.TextSearchHandler;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;
import javax.jcr.Node;
import java.util.List;

@Model(adaptables = Resource.class)
public interface TextSearchInterfaceModel {

    @Inject
    String getText();

    @Inject
    String[] getPaths();

    @Inject
    String getSearchApi();

    @Inject
    TextSearchHandler getTextSearchHandler();

    List<Node> getSearchResults();
}
