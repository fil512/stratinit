package com.kenstevens.stratinit.site.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.type.SectorCoords;

@Component
public class CityMover {
	@Autowired
	private ActionFactory actionFactory;


	public void setCityMove(City city, SectorCoords coords) {
		actionFactory.setCityMove(city, coords);
	}


}
