package com.kenstevens.stratinit.client.site.mover;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.site.action.post.ActionFactory;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CityMover {
	@Autowired
	private ActionFactory actionFactory;


	public void setCityMove(City city, SectorCoords coords) {
		actionFactory.setCityMove(city, coords);
	}


}
