package com.kenstevens.stratinit.client.audio;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.Info;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Service
public class WavLibrary {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Map<String, Info> infoMap = Maps.newHashMap();

	public Info getInfo(String filename) {
		Info info = infoMap.get(filename);
		if (info == null) {
			info = loadInfo(filename);
			if (info != null) {
				infoMap.put(filename, info);
			}
		}
		return info;
	}

	private Info loadInfo(String filename) {

		AudioInputStream sound = null;
		try {
			sound = getAudioInputStream(filename);
			if (sound == null) {
				return null;
			}
			return new DataLine.Info(Clip.class, sound.getFormat());
		} catch (UnsupportedAudioFileException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if (sound != null) {
					sound.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}

		return null;
	}

	protected InputStream getAudioStream(String filename) {
		return this.getClass().getResourceAsStream("wav/" + filename);
	}

	public AudioInputStream getAudioInputStream(String filename) throws UnsupportedAudioFileException, IOException {
		return AudioSystem.getAudioInputStream(getAudioStream(filename));
	}

}
