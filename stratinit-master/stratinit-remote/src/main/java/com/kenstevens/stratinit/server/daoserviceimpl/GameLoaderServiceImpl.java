package com.kenstevens.stratinit.server.daoserviceimpl;

import com.kenstevens.stratinit.cache.GameCache;
import com.kenstevens.stratinit.cache.GameLoader;
import com.kenstevens.stratinit.cache.GameLoaderImpl;
import com.kenstevens.stratinit.repo.*;
import com.kenstevens.stratinit.server.daoservice.GameLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


@Service
public class GameLoaderServiceImpl implements GameLoader, GameLoaderService {
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
	private SectorSeenRepo secorSeenDal;

	private GameLoader gameLoader;

	@SuppressWarnings("unused")
	@PostConstruct
	private void init() {
		gameLoader = new GameLoaderImpl(gameRepo, nationRepo, relationRepo, sectorDal, unitDal, cityRepo, secorSeenDal);
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
