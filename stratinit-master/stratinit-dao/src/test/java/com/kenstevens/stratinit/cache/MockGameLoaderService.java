package com.kenstevens.stratinit.cache;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.kenstevens.stratinit.dal.GameDal;
import com.kenstevens.stratinit.dal.SectorDal;
import com.kenstevens.stratinit.dal.UnitDal;

public class MockGameLoaderService implements GameLoader {
	@Autowired
	private GameDal gameDal;
	@Autowired
	private SectorDal sectorDal;
	@Autowired
	private UnitDal unitDal;

	private GameLoader gameLoader;

	@SuppressWarnings("unused")
	@PostConstruct
	private void init() {
		gameLoader = new GameLoaderImpl(gameDal, sectorDal, unitDal);
	}

	@Override
	public void flush(GameCache gameCache) {
		gameLoader.flush(gameCache);
	}

	@Override
	public GameCache loadGame(int gameId) {
		return gameLoader.loadGame(gameId);
	}
}
