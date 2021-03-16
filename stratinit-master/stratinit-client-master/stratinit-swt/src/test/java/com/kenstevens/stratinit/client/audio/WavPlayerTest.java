package com.kenstevens.stratinit.client.audio;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sound.sampled.DataLine.Info;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
public class WavPlayerTest {
    @Autowired
    WavLibrary wavLibrary;

    @Test
    public void getAudioStream() {
        Info info = wavLibrary.getInfo("Hit.wav");
        assertNotNull(info);
        assertEquals(1, info.getFormats().length);
        InputStream stream = wavLibrary.getAudioStream("Hit.wav");
        assertNotNull(stream);
	}
}
