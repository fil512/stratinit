package com.kenstevens.stratinit.server.remote.helper;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dto.SILaunchedSatellite;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.model.LaunchedSatellite;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.remote.StratInit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class PlayerWorldViewUpdate {
	@Autowired
	private StratInit stratInit;
	@Autowired
	private PlayerWorldView playerWorldView;
	@Autowired
	private PlayerNationList playerNationList;
	@Autowired
	private UnitDao unitDao;
	@Autowired
	private GameDao gameDao;

	public SIUpdate getWorldViewUpdate(Nation nation) {
		SIUpdate siupdate = new SIUpdate();
		siupdate.nationId = nation.getNationId();
		// TODO OPT instead of null, pass in world view, but then ensure all
		// sectorViews that were touched by units are refreshed
		siupdate.sectors = playerWorldView.getWorldViewSectors(nation);
		siupdate.cities = stratInit.getCities().getValue();
		siupdate.nations = playerNationList.getNations(nation, false);
		siupdate.relations = stratInit.getRelations().getValue();
		siupdate.units = stratInit.getUnits().getValue();
		siupdate.seenUnits = stratInit.getSeenUnits().getValue();
		siupdate.launchedSatellites = getLaunchedSatellites(nation);
		return siupdate;
	}

	private List<SILaunchedSatellite> getLaunchedSatellites(Nation nation) {
		Collection<LaunchedSatellite> sats = Lists.newArrayList(unitDao.getSatellites(nation));
		for (Nation ally : gameDao.getAllies(nation)) {
			sats.addAll(unitDao.getSatellites(ally));
		}
		List<SILaunchedSatellite> retval = new ArrayList<SILaunchedSatellite>();
		for (LaunchedSatellite sat : sats) {
			retval.add(new SILaunchedSatellite(sat));
		}
		return retval;
	}
}
