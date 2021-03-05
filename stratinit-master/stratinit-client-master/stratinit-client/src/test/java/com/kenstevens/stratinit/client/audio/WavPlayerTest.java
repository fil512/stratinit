package com.kenstevens.stratinit.client.audio;

import com.kenstevens.stratinit.BaseStratInitClientTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sound.sampled.DataLine.Info;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class WavPlayerTest extends BaseStratInitClientTest {
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
