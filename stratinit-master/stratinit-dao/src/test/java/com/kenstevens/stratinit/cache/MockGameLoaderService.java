package com.kenstevens.stratinit.cache;

import com.kenstevens.stratinit.repo.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public class MockGameLoaderService implements GameLoader {
	@Autowired
	private GameRepo gameRepo;
	@Autowired
	private NationRepo nationRepo;
	@Autowired
	private RelationRepo relationRepo;
	@Autowired
	private SectorDal sectorDal;
	@Autowired
	private UnitDal unitDal;
	@Autowired
	private CityRepo cityRepo;
	@Autowired
	private SectorSeenRepo sectorSeenRepo;

	private GameLoader gameLoader;

	@SuppressWarnings("unused")
	@PostConstruct
	private void init() {
		gameLoader = new GameLoaderImpl(gameRepo, nationRepo, relationRepo, sectorDal, unitDal, cityRepo, sectorSeenRepo);
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
