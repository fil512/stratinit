package com.kenstevens.stratinit.audio;

import com.kenstevens.stratinit.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.Info;
import java.io.IOException;

@Component("WavPlayer")
public class WavPlayer {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private Account account;
	@Autowired
	private WavLibrary wavLibrary;

	private boolean enabled = true;

	private void playFile(String filename) {
		if (!account.getPreferences().isPlaySounds()) {
			return;
		}
		if (!enabled) {
			return;
		}
		Info info = wavLibrary.getInfo(filename);
		if (info == null) {
			enabled = false;
			return;
		}
		Clip clip = null;
		try {
			AudioInputStream sound = wavLibrary.getAudioInputStream(filename);
			if (sound == null) {
				enabled = false;
				return;
			}
			clip = (Clip) AudioSystem.getLine(info);
			if (clip == null) {
				enabled = false;
				return;
			}
			clip.open(sound);

			// play the sound clip
			clip.start();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			enabled = false;
		} catch (LineUnavailableException e) {
			logger.error(e.getMessage(), e);
			enabled = false;
		} catch (UnsupportedAudioFileException e) {
			logger.error(e.getMessage(), e);
			enabled = false;
		}
	}

	public void shutdown() {
		// TODO sound
	}

	public void playHit() {
		playFile("Hit.wav");
	}

	public void playExplosion() {
		playFile("Explosion.wav");
	}

	public void playIntro() {
		playFile("Intro.wav");
	}

	public void playEmpty() {
		playFile("Clank.wav");
	}

	public void playRedAlert() {
		playFile("RedAlert.wav");
	}

	public void playIDied() {
		playFile("IDied.wav");
	}

	public void playFanfare() {
		if (account.getPreferences().isLiberator()) {
			playFile("Fanfare.wav");
		} else {
			playFile("Panic.wav");
		}
	}

	public void playFinishedLoading() {
		playFile("FinishedLoading.wav");
	}
}
