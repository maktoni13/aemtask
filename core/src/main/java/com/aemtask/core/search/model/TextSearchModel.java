package com.aemtask.core.search.model;

import com.aemtask.core.search.service.TextSearchHandler;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import java.util.List;


/**
 * Text search model component for text search configuration
 */
@Model(adaptables = Resource.class)
@Getter
@EqualsAndHashCode
@ToString
public class TextSearchModel {

    @Inject
    private String text;

    @Inject
    private String[] paths;

    @Inject
    private String searchApi;

    @Inject
    private TextSearchHandler textSearchHandler;

    private List<Node> searchResults;

    @PostConstruct
    public void init(){
        searchResults = textSearchHandler.getResult(getText(), getPaths(), getSearchApi());
    }

}
