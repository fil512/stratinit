package com.kenstevens.stratinit.server.daoservice.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.server.daoservice.GameCreator;

@Service
public class GameCreatorImpl implements GameCreator {
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private GameDao gameDao;
	
	@Override
	public void createGameIfAllMapped() {
		List<Game> games = gameDao.getAllGames();
		for (Game game : games) {
			if (!game.isMapped()) {
				return;
			}
		}
		Game game = new Game();
		gameDao.persist(game);
		logger.info("Created game "+game.getName());
	}

}
