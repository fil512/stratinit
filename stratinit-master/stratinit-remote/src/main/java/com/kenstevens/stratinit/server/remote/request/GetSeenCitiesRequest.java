package com.kenstevens.stratinit.server.remote.request;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.server.remote.helper.PlayerCityList;

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
