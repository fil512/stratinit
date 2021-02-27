package com.kenstevens.stratinit.server.rest.request;

import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.server.rest.helper.PlayerCityList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetSeenCitiesRequest extends PlayerRequest<List<SICity>> {
	@Autowired
	private PlayerCityList playerCityList;

	@Override
	protected List<SICity> execute() {
		return playerCityList.getSeenCities(getNation());
	}
}
