package com.kenstevens.stratinit.server.daoserviceimpl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.cache.GameCache;
import com.kenstevens.stratinit.cache.GameLoader;
import com.kenstevens.stratinit.cache.GameLoaderImpl;
import com.kenstevens.stratinit.dal.GameDal;
import com.kenstevens.stratinit.dal.SectorDal;
import com.kenstevens.stratinit.dal.UnitDal;
import com.kenstevens.stratinit.server.daoservice.GameLoaderService;


@Service
public class GameLoaderServiceImpl implements GameLoader, GameLoaderService {
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
