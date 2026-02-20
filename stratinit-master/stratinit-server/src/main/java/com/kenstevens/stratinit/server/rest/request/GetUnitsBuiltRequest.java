package com.kenstevens.stratinit.server.rest.request;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.audit.UnitBuildAudit;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dto.SIUnitBuilt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Scope("prototype")
@Component
public class GetUnitsBuiltRequest extends PlayerRequest<List<SIUnitBuilt>> {
	@Autowired
	private UnitDao unitDao;

	@Override
	protected List<SIUnitBuilt> execute() {
		Nation me = getNation();
		List<UnitBuildAudit> builds = unitDao.getBuildAudits(me.getGameId(), me
				.toString());
		return builds.stream()
				.map(SIUnitBuilt::new)
				.collect(Collectors.toList());
	}
}
