package com.kenstevens.stratinit.main;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.kenstevens.stratinit.audio.WavPlayer;
import com.kenstevens.stratinit.site.ActionQueue;
import com.kenstevens.stratinit.ui.MainWindow;

public final class StratInitClient {
	private static Logger logger = Logger.getLogger(StratInitClient.class.getName());

	private StratInitClient() {}

	public static void main(String[] args) {
		ActionQueue actionQueue = null;
		WavPlayer wavPlayer = null;
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext("/spring.xml");
			MainWindow window = (MainWindow) context.getBean("MainWindow");
			actionQueue = (ActionQueue) context.getBean("ActionQueue");
			actionQueue.start();
			wavPlayer = (WavPlayer) context.getBean("WavPlayer");
			window.open();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
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
