package com.kenstevens.stratinit.main;

import com.kenstevens.stratinit.SpringConfig;
import com.kenstevens.stratinit.audio.WavPlayer;
import com.kenstevens.stratinit.site.ActionQueue;
import com.kenstevens.stratinit.site.action.ActionFactory;
import com.kenstevens.stratinit.ui.MainShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public final class StratInitClient {
	static final Logger logger = LoggerFactory.getLogger(StratInitClient.class);

	private StratInitClient() {
	}

	public static void main(String[] args) {
		ActionQueue actionQueue = null;
		WavPlayer wavPlayer = null;
		ActionFactory actionFactory = null;
		try {
			AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
			context.register(SpringConfig.class);
			context.refresh();
			actionFactory = (ActionFactory) context.getBean("actionFactory");
			MainShell mainShell = (MainShell) context.getBean("MainShell");
			actionQueue = (ActionQueue) context.getBean("ActionQueue");
			wavPlayer = (WavPlayer) context.getBean("WavPlayer");

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
			if (wavPlayer != null) {
				wavPlayer.shutdown();
			}
			System.exit(0);
		}
	}

}
