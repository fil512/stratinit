package com.kenstevens.stratinit.server.remote.event;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Relation;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.server.daoservice.GameDaoService;
import com.kenstevens.stratinit.server.daoservice.LogDaoService;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;

@Service
public class GameEnder {
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private SectorDao sectorDao;

	@Autowired
	private GameDaoService gameDaoService;
	@Autowired
	private UnitDaoService unitDaoService;
	@Autowired
	private UnitDao unitDao;
	@Autowired
	private GameDao gameDao;

	@Autowired
	private LogDaoService logDaoService;
	@Autowired
	private SectorDaoService sectorDaoService;
	@Autowired
	private GameArchiver gameArchiver;

	public void endGame(Game game) {
		logger.info("Ending game "+game);
		gameArchiver.archive(game);
		gameDaoService.score(game);
		removeUnitsSeen(game);
		removeUnitMoves(game);
		removeRelations(game);
		removeLogs(game);
		removeUnits(game);
		removeCities(game);
		gameDaoService.disable(game);
	}




	private void removeCities(Game game) {
		for (City city : Lists.newArrayList(sectorDao.getCities(game))) {
			sectorDaoService.remove(city);
		}

	}


	private void removeRelations(Game game) {
		for (Relation relation : Lists.newArrayList(gameDao.getRelations(game))) {
			gameDaoService.remove(relation);
		}
	}


	private void removeUnitsSeen(Game game) {
		unitDaoService.removeUnitsSeen(game);
	}

	private void removeUnitMoves(Game game) {
		unitDaoService.removeUnitMoves(game);
	}


	private void removeUnits(Game game) {
		for (Unit unit : Lists.newArrayList(unitDao.getUnits(game))) {
			unitDaoService.remove(unit);
		}
	}

	// TODO REF Push remove lists down to dao like this method
	private void removeLogs(Game game) {
		logDaoService.removeLogs(game);
	}
}
