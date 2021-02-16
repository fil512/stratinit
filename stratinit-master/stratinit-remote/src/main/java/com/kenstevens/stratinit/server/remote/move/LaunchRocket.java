package com.kenstevens.stratinit.server.remote.move;

import com.google.common.collect.Sets;
import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.model.LaunchedSatellite;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.MessageDaoService;
import com.kenstevens.stratinit.server.daoservice.MoveSeen;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.server.remote.helper.NukeTargetChooser;
import com.kenstevens.stratinit.server.remote.helper.NukeTargetScore;
import com.kenstevens.stratinit.type.CoordMeasure;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Scope("prototype")
@Component
public class LaunchRocket {
	@Autowired
	private UnitDaoService unitDaoService;
	@Autowired
	private SectorDaoService sectorDaoService;
	@Autowired
	private MessageDaoService messageDaoService;
	@Autowired
	private DataCache dataCache;
	@Autowired
	NukeTargetChooser nukeTargetChooser;
	@Autowired
	UnitCommandFactory unitCommandFactory;

	private final Unit unit;
	private final SectorCoords targetCoords;
	private final MoveSeen moveSeen;
	private final CoordMeasure coordMeasure;

	public LaunchRocket(CoordMeasure coordMeasure, Unit unit, SectorCoords targetCoords, MoveSeen moveSeen) {
		this.coordMeasure = coordMeasure;
		this.unit = unit;
		this.targetCoords = targetCoords;
		this.moveSeen = moveSeen;

	}

	public Result<None> launch(boolean initialAttack) {
		if (unit.getCoords().distanceTo(coordMeasure, targetCoords) > unit.getMobility()) {
			return new Result<None>(unit+" can not reach "+targetCoords, false);
		}
		if (unit.getType() == UnitType.SATELLITE) {
			messageDaoService.postBulletin(unit.getParentGame(), unit.getNation() + " launched a " + unit.toString(), null);
			launchSatellite();
		} else if (unit.devastates()) {
			messageDaoService.postBulletin(unit.getParentGame(), unit.getNation() + " launched an " + unit.toString(), null);
			Set<Nation> hitCities = launchICBM(initialAttack);
			if (initialAttack) {
				counterLaunch(hitCities);
			}
		} else {
			return new Result<None>(unit+" is not a satellite and does not devastate.", false);
		}
		return new Result<None>("Launched "+unit+" to "+targetCoords, true);
	}

	private void counterLaunch(Set<Nation> hitCities) {
		for (Nation nation : hitCities) {
			NukeTargetScore target= nukeTargetChooser.chooseTarget(nation);
			launch(target);
		}
	}

	private void launch(NukeTargetScore target) {
		if (target == null) {
			return;
		}
		LaunchRocket launchRocket = unitCommandFactory.getLaunchRocket(coordMeasure, target.getNuke(), target.getCoords(), null);
		launchRocket.launch(false);
	}

	private Set<Nation> launchICBM(boolean initialAttack) {
		List<Sector> devastated = dataCache.getWorld(unit.getGameId()).getSectorsWithin(targetCoords, unit.getBlastRadius(), true);
		Set<Nation> hitCities = Sets.newHashSet();
		for (Sector sector : devastated) {
			hitCities.addAll(sectorDaoService.devastate(unit, sector, initialAttack));
		}
		unitDaoService.killUnit(unit);
		return hitCities;
	}

	private void launchSatellite() {
		LaunchedSatellite satellite = new LaunchedSatellite(unit.getNation(), targetCoords);
		unitDaoService.killUnit(unit);
		unitDaoService.persist(satellite);
		sectorDaoService.satelliteSees(satellite, moveSeen);
	}

}
