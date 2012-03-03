package com.kenstevens.stratinit.site.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.event.WorldArrivedEvent;
import com.kenstevens.stratinit.model.Account;

@Service
public class UpdateProcessor extends Processor {
	@Autowired
	private CityListProcessor cityListProcessor;
	@Autowired
	private NationListProcessor nationListProcessor;
	@Autowired
	private SectorListProcessor sectorListProcessor;
	@Autowired
	private UnitListProcessor unitListProcessor;
	@Autowired
	private SeenUnitListProcessor seenUnitListProcessor;
	@Autowired
	private RelationListProcessor relationListProcessor;
	@Autowired
	private SatelliteProcessor satelliteProcessor;
	@Autowired
	private Account account;
	@Autowired
	private FogOfWar fogOfWar;

	public void process(SIUpdate update, boolean firstTime) {
		db.setNationId(update.nationId);
		nationListProcessor.process(update.nations);
		relationListProcessor.process(update.relations);
		unitListProcessor.process(update.units, firstTime);
		sectorListProcessor.process(update.sectors);
		cityListProcessor.process(update.cities);
		seenUnitListProcessor.process(update.seenUnits);
		satelliteProcessor.process(update.launchedSatellites);
		if (account.getPreferences().isShowFOW()) {
			fogOfWar.setFogOfWar();
		}

		db.setLoaded(true);
		arrivedDataEventAccumulator.addEvent(new WorldArrivedEvent());
	}
}
