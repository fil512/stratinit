package com.kenstevens.stratinit.wicket.provider;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.audit.UnitBuildAudit;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.type.UnitBaseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerUnitsProvider {
	@Autowired
	UnitDao unitDao;
	@Autowired
	GameDao gameDao;

	private BuildAuditsAggregator getBuildUnitsAggregator(Game game,
			String username) {
		List<UnitBuildAudit> buildAudits = unitDao.getBuildAudits(game.getId(),
				username);

		BuildAuditsAggregator buildAuditsAggregator = new BuildAuditsAggregator(game.getStartTime(),
				buildAudits);
		return buildAuditsAggregator;
	}

	public List<List<Object>> getFullUnitsByNation(UnitBaseType unitBaseType, int gameId, String username) {
		Game game = gameDao.findGame(gameId);
		if (!game.hasEnded()) {
			return Lists.newArrayList();
		}
		return getBuildUnitsAggregator(game, username).getFullUnitsBuiltByDay(unitBaseType);
	}

	public List<DayUnitsListRow> getDayUnitsListRows(int gameId, String username, UnitBaseType unitBaseType) {
		Game game = gameDao.findGame(gameId);
		if (!game.hasEnded()) {
			return Lists.newArrayList();
		}
		return getBuildUnitsAggregator(game, username).getDayUnitsListRows(unitBaseType);
	}

}
