package com.kenstevens.stratinit.server.remote.helper;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dto.SISector;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.SectorSeen;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;

@Service
public class PlayerWorldView {
	@Autowired
	private SectorDao sectorDao;
	@Autowired
	private SectorDaoService sectorDaoService;
	@Autowired
	private GameDao gameDao;

	public List<SISector> getWorldViewSectors(Nation nation) {
		WorldSeenMap worldSeenMap = getWorldSeenMap(nation);

		return worldSeenMap.sectorsToSISectors();
	}

	public WorldSeenMap getWorldSeenMap(Nation nation) {
		WorldSeenMap worldSeenMap = new WorldSeenMap(sectorDaoService.getSeenWorldView(nation));
		worldSeenMap.merge(sectorDao.getSectorsSeen(nation));
		Collection<Nation> allies = gameDao.getAllies(nation);
		for (Nation ally : allies) {
			WorldView allyWorldView = sectorDaoService.getSeenWorldView(ally);
			Collection<SectorSeen> allySectorsSeen = sectorDao
					.getSectorsSeen(ally);
			worldSeenMap.merge(allySectorsSeen, allyWorldView);
		}
		return worldSeenMap;
	}
}
