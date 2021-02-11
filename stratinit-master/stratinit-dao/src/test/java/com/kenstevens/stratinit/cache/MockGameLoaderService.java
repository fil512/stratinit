package com.kenstevens.stratinit.cache;

import com.kenstevens.stratinit.dal.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public class MockGameLoaderService implements GameLoader {
	@Autowired
	private GameDal gameDal;
	@Autowired
	private NationDal nationDal;
	@Autowired
	private RelationDal relationDal;
	@Autowired
	private SectorDal sectorDal;
	@Autowired
	private UnitDal unitDal;
	@Autowired
	private CityDal cityDal;

	private GameLoader gameLoader;

	@SuppressWarnings("unused")
	@PostConstruct
	private void init() {
		gameLoader = new GameLoaderImpl(gameDal, nationDal, relationDal, sectorDal, unitDal, cityDal);
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
