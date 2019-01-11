package com.aemtask.core.search.service.impl;

import com.aemtask.core.search.config.TextSearchConfig;
import com.aemtask.core.search.service.TextSearchHandler;
import com.aemtask.core.search.service.TextSearchProvider;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import javax.jcr.Node;
import java.util.ArrayList;
import java.util.List;

/**
 * Text search handler implementation for multi implementation usage
 */
@Component(
        service = {TextSearchHandler.class},
        immediate = true
)
public class TextSearchHandlerImpl implements TextSearchHandler {

    @Reference
    private TextSearchConfig textSearchConfig;

    private List<TextSearchProvider> textSearchProviderList;

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    protected void bind(TextSearchProvider textSearchProvider) {
        if (textSearchProviderList == null) {
            textSearchProviderList = new ArrayList<>();
        }
        textSearchProviderList.add(textSearchProvider);
    }

    protected void unbind(TextSearchProvider textSearchProvider) {
        textSearchProviderList.remove(textSearchProvider);
    }

    @Override
    public List<Node> getResult() {
        return getResult(textSearchConfig.getText(),
                textSearchConfig.getPaths(),
                textSearchConfig.getSearchAPI());
    }

    @Override
    public List<Node> getResult(String text, String[] paths, String searchApi) {
        for (TextSearchProvider textSearchProviderImpl : textSearchProviderList) {
            if (textSearchProviderImpl.implementsApi(searchApi)) {
                return textSearchProviderImpl.getResult(paths, text);
            }
        }
        return null;
    }

}
