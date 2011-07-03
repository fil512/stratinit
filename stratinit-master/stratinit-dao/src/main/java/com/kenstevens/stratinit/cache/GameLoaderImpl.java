package com.kenstevens.stratinit.cache;

import java.util.List;

import org.apache.log4j.Logger;

import com.kenstevens.stratinit.dal.GameDal;
import com.kenstevens.stratinit.dal.SectorDal;
import com.kenstevens.stratinit.dal.UnitDal;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.CityMove;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Relation;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitMove;

public class GameLoaderImpl implements GameLoader {
	private final Logger logger = Logger.getLogger(getClass());
	private final GameDal gameDal;
	private final SectorDal sectorDal;
	private final UnitDal unitDal;

	public GameLoaderImpl(GameDal gameDal, SectorDal sectorDal, UnitDal unitDal) {
		this.gameDal = gameDal;
		this.sectorDal = sectorDal;
		this.unitDal = unitDal;
	}

	public GameCache loadGame(int gameId) {
		logger.info("Loading game #" + gameId + " into cache.");

		Game game = gameDal.findGame(gameId);
		if (game == null) {
			return null;
		}
		logger.info("Getting Relations");
		List<Relation> relations = gameDal.getRelations(game);
		GameCache gameCache = new GameCache(game, relations);
		logger.info("Getting Nations");
		gameCache.addNations(gameDal.getNations(game));
		if (game.isMapped()) {
			if (game.getSize() == 0) {
				logger.error("Cannot load game "+game+" because it is mapped but has no size.");
				return null;
			}
			logger.info("Getting World");
			gameCache.setWorld(sectorDal.getWorld(game));
			logger.info("Getting Cities");
			setCities(gameCache);
			logger.info("Getting Units");
			setUnits(gameCache);
			logger.info("Getting Sectors Seen");
			gameCache.setSectorsSeen(sectorDal.getSectorsSeen(gameCache
					.getGame()));
			logger.info("Getting Units Seen");
			gameCache.setUnitsSeen(unitDal.getUnitsSeen(gameCache.getGame()));
			logger.info("Getting Launched Satellites");
			gameCache.setLaunchedSatellites(unitDal.getSatellites(gameCache
					.getGame()));
			logger.info("Getting Units Move");
			List<UnitMove> badUnitMoves = gameCache.setUnitsMove(unitDal.getUnitsMove(gameCache.getGame()));
			clearBadUnitMoves(badUnitMoves);
			logger.info("Getting City Moves");

			List<CityMove> badCityMoves = gameCache.setCityMoves(sectorDal.getCityMoves(gameCache.getGame()));
			clearBadCityMoves(badCityMoves);
}
		logger.info("Game #" +gameId +" loaded.");
		return gameCache;
	}

	private void clearBadUnitMoves(List<UnitMove> badUnitMoves) {
		for (UnitMove unitMove : badUnitMoves) {
			logger.warn("Removing bad unit move id#"+unitMove.getId());
			unitDal.remove(unitMove);
		}
	}

	private void clearBadCityMoves(List<CityMove> badCityMoves) {
		for (CityMove cityMove : badCityMoves) {
			logger.warn("Removing bad city move id#"+cityMove.getId());
			sectorDal.remove(cityMove);
		}
	}

	private void setCities(GameCache gameCache) {
		List<City> cities = sectorDal.getCities(gameCache.getGame());
		for (City city : cities) {
			gameCache.add(city);
		}
	}

	private void setUnits(GameCache gameCache) {
		List<Unit> units = unitDal.getUnits(gameCache.getGame());
		for (Unit unit : units) {
			gameCache.add(unit);
		}
	}

	public void flush(GameCache gameCache) {
		gameCache.flush(gameDal, sectorDal);
		for (NationCache nationCache : gameCache.getNationCaches()) {
			nationCache.flush(gameCache.getGameId(), gameDal, sectorDal, unitDal);
		}
	}
}
