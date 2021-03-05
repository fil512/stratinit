package com.kenstevens.stratinit.client.server.rest.request;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.audit.UnitBuildAudit;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dto.SIUnitBuilt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

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
		return Lists.newArrayList(Collections2.transform(builds, new Function<UnitBuildAudit, SIUnitBuilt>() {
					@Override
					public SIUnitBuilt apply(UnitBuildAudit build) {
						return new SIUnitBuilt(build);
					}
				}
		));
	}
}
