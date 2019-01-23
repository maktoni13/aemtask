package com.aemtask.core.search.service.impl;

import com.aemtask.core.search.config.TextSearchConfig;
import com.aemtask.core.search.service.TextSearchHandler;
import com.aemtask.core.search.service.TextSearchProvider;
import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TextSearchHandlerImplTest {

    private static final String SEARCH_API_QB = "QueryBuilder";
    private static final String SEARCH_API_QM = "QueryManager";
    private static final String TEXT = "";
    private static final String[] PATHS = new String[2];

    @Rule
    public final OsgiContext context = new OsgiContext();

    @Mock
    private TextSearchConfig mockTextSearchConfig;

    @Mock
    private QueryBuilderTextSearchProviderImpl mockQueryBuilderTextSearchProvider;

    @Mock
    private QueryManagerTextSearchProviderImpl mockQueryManagerTextSearchProvider;

    @Spy
    private List<TextSearchProvider> mockTextSearchProviderList = new ArrayList<>();

    @InjectMocks
    private TextSearchHandlerImpl textSearchHandler;

    @Before
    public void setup() throws Exception{
        when(mockQueryBuilderTextSearchProvider.implementsApi(SEARCH_API_QB)).thenReturn(true);
        mockTextSearchProviderList.add(mockQueryBuilderTextSearchProvider);
        mockTextSearchProviderList.add(mockQueryManagerTextSearchProvider);
    }

    @Test
    public void shouldCallQueryBuilderImplementation(){
        textSearchHandler.getResult(TEXT, PATHS, SEARCH_API_QB);
        verify(mockQueryBuilderTextSearchProvider).getResult(PATHS, TEXT);
    }

    @Test
    public void shouldNotCallQueryManagerImplementation(){
        textSearchHandler.getResult(TEXT, PATHS, SEARCH_API_QB);
        verify(mockQueryManagerTextSearchProvider, never()).getResult(PATHS, TEXT);
    }
}