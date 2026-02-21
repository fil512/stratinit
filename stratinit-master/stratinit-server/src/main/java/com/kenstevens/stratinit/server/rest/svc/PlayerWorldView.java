package com.kenstevens.stratinit.server.rest.svc;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.SectorSeen;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.RelationDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dto.SISector;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.server.service.SectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class PlayerWorldView {
	@Autowired
	private SectorDao sectorDao;
	@Autowired
	private SectorService sectorService;
	@Autowired
	private GameDao gameDao;
	@Autowired
	private RelationDao relationDao;

	public List<SISector> getWorldViewSectors(Nation nation) {
		WorldSeenMap worldSeenMap = getWorldSeenMap(nation);

		return worldSeenMap.sectorsToSISectors();
	}

	public WorldSeenMap getWorldSeenMap(Nation nation) {
		WorldSeenMap worldSeenMap = new WorldSeenMap(sectorService.getSeenWorldView(nation));
		worldSeenMap.merge(sectorDao.getSectorsSeen(nation));
		Collection<Nation> allies = relationDao.getAllies(nation);
		for (Nation ally : allies) {
			WorldView allyWorldView = sectorService.getSeenWorldView(ally);
			Collection<SectorSeen> allySectorsSeen = sectorDao
					.getSectorsSeen(ally);
			worldSeenMap.merge(allySectorsSeen, allyWorldView);
		}
		return worldSeenMap;
	}
}
