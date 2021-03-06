package com.kenstevens.stratinit.server.rest.request;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.kenstevens.stratinit.client.model.LaunchedSatellite;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dto.SISatellite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

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
		return Lists.newArrayList(Collections2.transform(satellites, new Function<LaunchedSatellite, SISatellite>() {
			public SISatellite apply(LaunchedSatellite satellite) {
				return new SISatellite(satellite);
			}
		}));
	}
}
