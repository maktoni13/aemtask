package com.aemtask.core.i18n.service.impl;

import com.aemtask.core.i18n.service.RandomMoodMessage;
import org.osgi.service.component.annotations.Component;

import java.util.Random;

@Component(
        service = {RandomMoodMessage.class},
        immediate = true
)
public class RandomMoodMessageImpl implements RandomMoodMessage {
    private static final String[] MOOD_ARRAY = new String[]{":)", ":(", "%)", "=-)"};

    @Override
    public String getMessage(){
        return MOOD_ARRAY[new Random().nextInt(MOOD_ARRAY.length)];
    }
}
