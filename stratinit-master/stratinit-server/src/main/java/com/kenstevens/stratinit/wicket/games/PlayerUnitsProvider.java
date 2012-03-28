package com.kenstevens.stratinit.wicket.games;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.model.audit.UnitBuildAudit;

@Service
public class PlayerUnitsProvider {
	@Autowired
	UnitDao unitDao;

	public List<PlayerUnitCount> getUnitsByNation(int gameId, String username) {
		List<UnitBuildAudit> buildAudits = unitDao.getBuildAudits(gameId,
				username);

		BuildAuditsAggregator buildAuditsAggregator = new BuildAuditsAggregator(buildAudits);
		return buildAuditsAggregator.getUnitsBuiltByDay();
	}

}
