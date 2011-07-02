package com.kenstevens.stratinit.server.remote.event;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.CityPK;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;

@Scope("prototype")
@Component
public class BuildUnitEventUpdate extends EventUpdate {
	@Autowired
	private SectorDaoService sectorDaoService;

	private final Date date;
	private final CityPK cityPK;

	public BuildUnitEventUpdate(CityPK cityPK, Date date) {
		this.cityPK = cityPK;
		this.date = date;
	}

	@Override
	protected void executeWrite() {
		sectorDaoService.buildUnit(cityPK, date);
	}
}
