package com.kenstevens.stratinit.audio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import javax.sound.sampled.DataLine.Info;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kenstevens.stratinit.StratInitClientTest;


public class WavPlayerTest  extends StratInitClientTest {
	@Autowired
	WavLibrary wavLibrary;
	
	@Test
	public void getAudioStream() {
		Info info = wavLibrary.getInfo("Hit.wav");
		assertNotNull(info);
		assertEquals(1, info.getFormats().length);;
		InputStream stream = wavLibrary.getAudioStream("Hit.wav");
		assertNotNull(stream);
	}
}
