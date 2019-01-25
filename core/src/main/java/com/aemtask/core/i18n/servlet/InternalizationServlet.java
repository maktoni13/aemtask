package com.aemtask.core.i18n.servlet;

import com.aemtask.core.i18n.service.RandomMoodMessage;
import com.day.cq.i18n.I18n;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Internalization servlet implementation
 */
@Component(
        service = { Servlet.class },
        property={
                Constants.SERVICE_DESCRIPTION + "=Internalization test servlet"
                , "sling.servlet.resourceTypes=" + "sling/servlet/default"
                , "sling.servlet.methods=" + HttpConstants.METHOD_GET
                , "sling.servlet.extensions=" + "csv"
        })
public class InternalizationServlet extends SlingSafeMethodsServlet {

    private static final String CONTENT_TYPE = "text/html; charset=utf-8";
    private static final String HTML_MSG_TEMPLATE = "<p>%s</>";
    private static final String MSG_KEY_CSV_GREETING = "csv.greeting";
    private static final String MSG_HINT_REGULAR = "Regular";
    private static final String MSG_HINT_SPECIAL = "Special";
    private static final String ERROR_MSG_CANT_GET_PAGE = "Can't get page from resource (%s)";
    private static final String INFO_MSG_DEFINED_LOCALE = "Defined locale (locale = %s, resourcePath = %s)";

    private final Logger LOGGER = LoggerFactory.getLogger(InternalizationServlet.class);

    @Reference
    private RandomMoodMessage randomMoodMessage;

    @Override
    protected void doGet(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response) throws ServletException, IOException {

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(CONTENT_TYPE);

        String resourcePath = request.getRequestPathInfo().getResourcePath();
        Resource resource = request.getResourceResolver().resolve(resourcePath);
        Page page = resource.adaptTo(Page.class);

        if (page == null){
            LOGGER.error(String.format(ERROR_MSG_CANT_GET_PAGE, resourcePath));
            return;
        }

        PrintWriter printWriter = response.getWriter();
        Locale locale = page.getLanguage(false);
        LOGGER.info(String.format(INFO_MSG_DEFINED_LOCALE, locale.toString(), resourcePath));
        ResourceBundle resourceBundle = request.getResourceBundle(locale);
        I18n i18n = new I18n(resourceBundle);

        printMessage(printWriter, i18n, MSG_HINT_REGULAR);
        printMessage(printWriter, i18n, MSG_HINT_SPECIAL);

        printWriter.flush();
        printWriter.close();

    }

    private void printMessage(PrintWriter printWriter, I18n i18n, String hint) {
        String greeting = i18n.get(MSG_KEY_CSV_GREETING, hint, randomMoodMessage.getMessage());
        printWriter.write(String.format(HTML_MSG_TEMPLATE, greeting));
    }
}
