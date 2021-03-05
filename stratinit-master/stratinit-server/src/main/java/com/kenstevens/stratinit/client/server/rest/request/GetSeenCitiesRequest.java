package com.kenstevens.stratinit.client.server.rest.request;

import com.kenstevens.stratinit.client.server.rest.svc.CitySvc;
import com.kenstevens.stratinit.dto.SICity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetSeenCitiesRequest extends PlayerRequest<List<SICity>> {
	@Autowired
	private CitySvc citySvc;

	@Override
	protected List<SICity> execute() {
		return citySvc.getSeenCities(getNation());
	}
}
