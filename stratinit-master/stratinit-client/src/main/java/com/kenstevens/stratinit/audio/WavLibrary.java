package com.kenstevens.stratinit.audio;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

@Service
public class WavLibrary {
	private final Logger logger = Logger.getLogger(getClass());

	private Map<String, Info> infoMap = Maps.newHashMap();

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
