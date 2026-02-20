package com.kenstevens.stratinit.server.rest.request;

import com.kenstevens.stratinit.client.model.LaunchedSatellite;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dto.SISatellite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Scope("prototype")
@Component
public class GetSattelitesRequest extends PlayerRequest<List<SISatellite>> {
	@Autowired
	private UnitDao unitDao;

	@Override
	protected List<SISatellite> execute() {
		Nation nation = getNation();
		Collection<LaunchedSatellite> satellites = unitDao
				.getSatellites(nation);
		return satellites.stream()
				.map(SISatellite::new)
				.collect(Collectors.toList());
	}
}
