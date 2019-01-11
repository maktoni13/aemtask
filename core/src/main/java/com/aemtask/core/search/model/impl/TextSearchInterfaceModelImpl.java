package com.aemtask.core.search.model.impl;

import com.aemtask.core.search.service.TextSearchHandler;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import com.aemtask.core.search.model.TextSearchInterfaceModel;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import java.util.List;

@Model(adaptables = Resource.class)
@AllArgsConstructor
@NoArgsConstructor
public class TextSearchInterfaceModelImpl implements TextSearchInterfaceModel {

    @Inject
    private String text;
    @Inject
    private String[] paths;
    @Inject
    private String searchApi;
    @Inject
    private TextSearchHandler textSearchHandler;

    private List<Node> searchResults;

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String[] getPaths() {
        return paths;
    }

    @Override
    public String getSearchApi() {
        return searchApi;
    }

    @Override
    public TextSearchHandler getTextSearchHandler() {
        return textSearchHandler;
    }

    @Override
    public List<Node> getSearchResults() {
        return searchResults;
    }

    @PostConstruct
    public void init(){
        searchResults = textSearchHandler.getResult(getText(), getPaths(), getSearchApi());
    }


}

