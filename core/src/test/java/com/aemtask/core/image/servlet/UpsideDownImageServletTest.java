package com.aemtask.core.image.servlet;

import com.day.image.Layer;
import com.aemtask.core.image.service.ImageUpsideDownTurn;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;

import javax.servlet.ServletException;

import static org.mockito.Mockito.verify;

import java.io.IOException;


@RunWith(MockitoJUnitRunner.class)
public class UpsideDownImageServletTest {

    @Rule
    public final SlingContext context = new SlingContext();

    @Mock
    private ImageUpsideDownTurn imageUpsideDownTurn;

    @InjectMocks
    private UpsideDownImageServlet upsideDownImageServlet;

    @Test
    public void shouldCallDoRotateImageUpsideDownComponent() throws ServletException, IOException {
        context.build().resource("/content/test.ud.jpg", "jcr:title", "resource title").commit();
        context.currentResource("/content/test.ud.jpg");

        upsideDownImageServlet.doGet(context.request(), context.response());
        verify(imageUpsideDownTurn).doRotate(null);
    }

}