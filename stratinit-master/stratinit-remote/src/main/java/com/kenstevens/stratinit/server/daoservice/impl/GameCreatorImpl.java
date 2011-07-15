package com.kenstevens.stratinit.server.daoservice.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.server.daoservice.GameCreator;

@Service
public class GameCreatorImpl implements GameCreator {
	private final Log logger = LogFactory.getLog(getClass());

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
