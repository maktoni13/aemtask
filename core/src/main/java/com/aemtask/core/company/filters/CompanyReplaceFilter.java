package com.aemtask.core.company.filters;

import com.adobe.acs.commons.util.BufferingResponse;
import com.aemtask.core.company.config.CompanyNameConfig;
import org.apache.sling.engine.EngineConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet filter component that replaces company names in content
 */
@Component(service = Filter.class, property = {
        Constants.SERVICE_DESCRIPTION + "= Filter incoming requests and replace company name in response",
        EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST,
        EngineConstants.SLING_FILTER_PATTERN + "=/content/we-retail/.*",
        Constants.SERVICE_RANKING + "=-700"
})
public class CompanyReplaceFilter implements Filter {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final String CONTENT_TYPE_HTML = "html";
    private static final String CONTENT_TYPE_JSON = "json";

    @Reference
    private CompanyNameConfig companyNameConfig;

    public CompanyNameConfig getCompanyNameConfig() {
        return companyNameConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        PrintWriter responseWriter = response.getWriter();
        BufferingResponse bufferingResponse = new BufferingResponse(response);
        filterChain.doFilter(request, bufferingResponse);

        if (response.getContentType() != null
                && (response.getContentType().contains(CONTENT_TYPE_HTML)
                || response.getContentType().contains(CONTENT_TYPE_JSON))) {
            String content = bufferingResponse.getContents().replaceAll(getCompanyNameConfig().getSource(),
                    getCompanyNameConfig().getReplacement());
            response.setContentLength(content.length());
            responseWriter.write(content);
        }

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }
}
