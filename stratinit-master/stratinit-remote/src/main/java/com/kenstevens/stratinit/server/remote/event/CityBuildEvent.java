package com.kenstevens.stratinit.server.remote.event;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.CityPK;
import com.kenstevens.stratinit.type.UnitType;

@Scope("prototype")
@Component
public class CityBuildEvent extends Event {
	@Autowired
	private EventQueue eventQueue;
	@Autowired
	private StratInitUpdater stratInitUpdater;
	@Autowired
	private SectorDao sectorDao;

	CityBuildEvent(City city) {
		super(city);
	}

	@Override
	protected void execute() {
		CityPK cityPK = (CityPK)getEventKey().getKey();
		UnitType oldBuild = sectorDao.findCity(cityPK).getBuild();
		stratInitUpdater.buildUnit(cityPK, new Date());
		City city = sectorDao.findCity(cityPK);
		UnitType newBuild = city.getBuild();
		if (!newBuild.equals(oldBuild)) {
			this.cancel();
			eventQueue.schedule(city);
		}
	}

}
