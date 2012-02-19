package com.kenstevens.stratinit.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class GameNameFile {
	private static Log logger = LogFactory.getLog(GameNameFile.class);

	private static final String BATTLES_FILENAME = "battles.txt";
	private static final int MAX_GAMES = 4096;
	private static String[] gameNames;

	private GameNameFile() {
	}

	public static String getName(int id) {
		if (gameNames == null) {
			loadGameNames();
		}

		return gameNames[id];
	}

	private static void loadGameNames() {
		gameNames = new String[MAX_GAMES];
		for (int i = 0; i < MAX_GAMES; ++i) {
			gameNames[i] = "" + i;
		}
		InputStream inputStream = GameNameFile.class
		.getResourceAsStream(BATTLES_FILENAME);
		if (inputStream == null) {
			logger.error("Unable to open "+BATTLES_FILENAME);
			return;
		}
		try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream));
				String line;
				int i = 100;
				while ((line = reader.readLine()) != null) {
					gameNames[i++] = line.trim();
				}
		} catch (IOException e) {
			// ignore;
		}
	}
}
