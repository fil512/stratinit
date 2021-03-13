package com.kenstevens.stratinit.server.event;

import com.google.common.annotations.VisibleForTesting;
import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.CityPK;
import com.kenstevens.stratinit.dao.CityDao;
import com.kenstevens.stratinit.server.event.svc.EventQueue;
import com.kenstevens.stratinit.server.event.svc.StratInitUpdater;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Scope("prototype")
@Component
public class CityBuildEvent extends Event {
	@Autowired
	private EventQueue eventQueue;
	@Autowired
	private StratInitUpdater stratInitUpdater;
	@Autowired
	private CityDao cityDao;

	public CityBuildEvent(City city) {
		super(city);
	}

	@Override
	protected void execute() {
		CityPK cityPK = (CityPK) getEventKey().getKey();
		UnitType oldBuild = cityDao.findCity(cityPK).getBuild();
		stratInitUpdater.buildUnit(cityPK, new Date());
		City city = cityDao.findCity(cityPK);
		UnitType newBuild = city.getBuild();
		if (!newBuild.equals(oldBuild)) {
			this.cancel();
			eventQueue.schedule(city);
		}
	}

	@VisibleForTesting
	public EventQueue getEventQueueForUnitTest() {
		return eventQueue;
	}

}
