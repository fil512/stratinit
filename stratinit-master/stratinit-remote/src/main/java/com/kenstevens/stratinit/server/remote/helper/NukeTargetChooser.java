package com.kenstevens.stratinit.server.remote.helper;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Unit;

@Service
public class NukeTargetChooser {
	@Autowired
	SectorDao sectorDao;
	@Autowired
	UnitDao unitDao;
	@Autowired
	PlayerWorldView playerWorldView;
	
	public NukeTargetScore chooseTarget(Nation nation) {
		Collection<Unit> nukes = unitDao.getMissiles(nation);
		if (nukes.isEmpty()) {
			return null;
		}
		WorldSeenMap worldSeenMap = playerWorldView.getWorldSeenMap(nation);
		Collection<Unit> unitsSeen = unitDao.getSeenUnits(nation);
		TargetScore targetScore = new TargetScore(worldSeenMap, unitsSeen);
		targetScore.calculateScore();
		return targetScore.chooseTarget(nukes);
	}

}
