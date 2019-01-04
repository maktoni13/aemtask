package com.aemtask.core.image.service.impl;

import com.aemtask.core.image.service.ImageUpsideDownTurn;
import com.day.image.Layer;
import org.osgi.service.component.annotations.Component;

/**
 * Image Upside-down turn service component implementation
 */
@Component(
        service = { ImageUpsideDownTurn.class },
        immediate = true
)
public class ImageUpsideDownTurnImpl implements ImageUpsideDownTurn {

    private static final int UPSIDE_DOWN_DEGREES = 180;

    @Override
    public Layer doRotate(Layer layer) {
        layer.rotate(UPSIDE_DOWN_DEGREES);
        return layer;
    }
}
