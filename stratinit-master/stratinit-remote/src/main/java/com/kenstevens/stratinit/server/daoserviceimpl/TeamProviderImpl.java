package com.kenstevens.stratinit.server.daoserviceimpl;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.rank.TeamProvider;
import com.kenstevens.stratinit.repo.GameHistoryNationRepo;
import com.kenstevens.stratinit.repo.GameHistoryTeamRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class TeamProviderImpl implements TeamProvider {
	@Autowired
	GameHistoryTeamRepo gameHistoryTeamRepo;
	@Autowired
	GameHistoryNationRepo gameHistoryNationRepo;
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
	public void getTeamsAndNations(GameHistory gameHistory, List<SITeam> teams, List<SINation> nations) {
		List<GameHistoryTeam> gameHistoryTeams = gameHistoryTeamRepo.findByGameHistory(gameHistory);
		for (GameHistoryTeam gameHistoryTeam : gameHistoryTeams) {
			List<GameHistoryNation> gameHistoryNations = gameHistoryNationRepo.findByGameHistoryTeam(gameHistoryTeam);
			addTeam(teams, gameHistoryNations);
			addNations(nations, gameHistoryNations);
		}
	}

	private void addNations(List<SINation> nations,
			List<GameHistoryNation> gameHistoryNations) {
		for (GameHistoryNation gameHistoryNation : gameHistoryNations) {
			SINation nation = new SINation();
			nation.cities = gameHistoryNation.getCities();
			nation.name = gameHistoryNation.getName();
			nation.power = gameHistoryNation.getPower();
			nation.gameId = gameHistoryNation.getGameHistoryTeam().getGameHistory().getGameId();
			nations.add(nation);
		}

	}

	private void addTeam(List<SITeam> teams,
			List<GameHistoryNation> gameHistoryNations) {
		SITeam team = new SITeam();
		team.nation1 = gameHistoryNations.get(0).getName();
		team.score += gameHistoryNations.get(0).getCities();
		if (gameHistoryNations.size() > 1) {
			team.nation2 = gameHistoryNations.get(1).getName();
			team.score += gameHistoryNations.get(1).getCities();
		}
		teams.add(team);
	}

	@Override
	public List<SITeam> findTeams(GameHistory gameHistory) {
		List<SITeam> teams = Lists.newArrayList();
		List<SINation> nations = Lists.newArrayList();

		getTeamsAndNations(gameHistory, teams, nations);
		return teams;
	}

}
