package com.kenstevens.stratinit.server.rest.svc;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.util.UpdateCalculator;
import com.kenstevens.stratinit.dto.SIUpdate;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerWorldViewUpdate {
	@Autowired
	private PlayerWorldView playerWorldView;
	@Autowired
	private NationSvc nationSvc;
	@Autowired
	private CitySvc citySvc;
	@Autowired
	private SatelliteSvc satelliteSvc;
	@Autowired
	private RelationSvc relationSvc;
	@Autowired
	private UnitSvc unitSvc;

	public SIUpdate getWorldViewUpdate(Nation nation) {
		SIUpdate siupdate = new SIUpdate();

		siupdate.nationId = nation.getNationId();

		Game game = nation.getGame();
		siupdate.lastUpdated = game.getLastUpdated();
		siupdate.tickIntervalMs = UpdateCalculator.shrinkTime(game.isBlitz(), game.getUpdatePeriodMilliseconds());
		siupdate.buildTickIntervalMs = UpdateCalculator.shrinkTime(game.isBlitz(), DateUtils.MILLIS_PER_HOUR);
		siupdate.gameId = game.getId();
		siupdate.gameName = game.getGamename();
		siupdate.gameEnds = game.getEnds();
		siupdate.blitz = game.isBlitz();

		// TODO OPT instead of null, pass in world view, but then ensure all
		// sectorViews that were touched by units are refreshed

		siupdate.sectors = playerWorldView.getWorldViewSectors(nation);
		siupdate.nations = nationSvc.getNations(nation, true);
		siupdate.cities = citySvc.getCities(nation);
		siupdate.relations = relationSvc.getRelations(nation);
		siupdate.units = unitSvc.getUnits(nation);
		siupdate.seenUnits = unitSvc.getSeenUnits(nation);
		siupdate.launchedSatellites = satelliteSvc.getLaunchedSatellites(nation);
		siupdate.log = nationSvc.getBattleLogs(nation);

		return siupdate;
	}

}
