package com.kenstevens.stratinit.client.main;

import com.kenstevens.stratinit.client.SwtConfig;
import com.kenstevens.stratinit.client.api.IAudioPlayer;
import com.kenstevens.stratinit.client.site.ActionQueue;
import com.kenstevens.stratinit.client.site.action.post.ActionFactory;
import com.kenstevens.stratinit.client.ui.MainShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public final class StratInitClient {
	static final Logger logger = LoggerFactory.getLogger(StratInitClient.class);

	private StratInitClient() {
	}

	public static void main(String[] args) {
		ActionQueue actionQueue = null;
		IAudioPlayer audioPlayer = null;
		ActionFactory actionFactory = null;
		try {
			logger.info("Starting StratInitClient version {}", ClientConstants.CLIENT_VERSION);
			AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
			context.register(SwtConfig.class);
			context.refresh();
			actionFactory = (ActionFactory) context.getBean("actionFactory");
			MainShell mainShell = (MainShell) context.getBean("MainShell");
			actionQueue = (ActionQueue) context.getBean("ActionQueue");
			audioPlayer = (IAudioPlayer) context.getBean("WavPlayer");

			actionQueue.start();
			mainShell.start();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if (actionFactory != null) {
				actionFactory.submitError(e);
			}
		} finally {
			logger.info("Shutting down.");
			if (actionQueue != null) {
				actionQueue.shutdown();
			}
			if (audioPlayer != null) {
				audioPlayer.shutdown();
			}
		}
	}

}
