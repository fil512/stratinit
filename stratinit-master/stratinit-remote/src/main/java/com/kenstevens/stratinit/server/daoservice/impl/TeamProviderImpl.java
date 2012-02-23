package com.kenstevens.stratinit.server.daoservice.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.GameHistory;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.rank.TeamProvider;

@Service
public class TeamProviderImpl implements TeamProvider {
	@Autowired
	GameDao gameDao;
	@Autowired
	SectorDao sectorDao;
	
	@Override
	public Collection<Nation> getAllies(Nation nation) {
		return gameDao.getAllies(nation);
	}

	@Override
	public List<Nation> getNations(Game game) {
		return gameDao.getNations(game);
	}

	@Override
	public int getNumberOfCities(Nation nation) {
		return sectorDao.getNumberOfCities(nation);
	}

	@Override
	public List<SITeam> findTeams(GameHistory game) {
//		pull this down from GameArchiteListProvider
	}

}
