package com.aemtask.core.image.servlet;

import com.aemtask.core.image.service.ImageUpsideDownTurn;
import com.day.cq.wcm.foundation.Image;
import com.day.image.Layer;
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

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Image Upside-down turn servlet for aemtask images with ud selector
 */
@Component(
        service = { Servlet.class },
        property={
                Constants.SERVICE_DESCRIPTION + "=Upside-down images servlet"
                , "sling.servlet.methods=" + HttpConstants.METHOD_GET
                , "sling.servlet.resourceTypes=" + "aemtask/components/content/image"
                , "sling.servlet.selectors=" + "ud"
                , "sling.servlet.extensions=" + "jpeg"
                , "sling.servlet.extensions=" + "png"
        })
public class UpsideDownImageServlet extends SlingSafeMethodsServlet {

    private final Logger LOGGER = LoggerFactory.getLogger(UpsideDownImageServlet.class);

    private static final String LOGGER_INFO_MSG = "Image Upside-Down Servlet";
    private static final double QUALITY = 1.0;

    @Reference
    private ImageUpsideDownTurn imageUpsideDownTurn;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {

        LOGGER.info(LOGGER_INFO_MSG, request.getRequestURI());

        final Resource resource = request.getResource();
        Image image = new Image(resource);
        Layer layer = null;
        String mimeType = null;
        try {
            layer = this.imageUpsideDownTurn.doRotate(image.getLayer(false, false, false));
            mimeType = image.getMimeType();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

        if (layer != null && mimeType != null) {
            response.setContentType(mimeType);
            layer.write(mimeType, QUALITY, response.getOutputStream());
            response.flushBuffer();
        }

    }
}
