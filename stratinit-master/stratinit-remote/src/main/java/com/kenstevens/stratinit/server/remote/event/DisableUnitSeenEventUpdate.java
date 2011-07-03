package com.kenstevens.stratinit.server.remote.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.UnitSeenPK;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;

@Scope("prototype")
@Component
public class DisableUnitSeenEventUpdate extends EventUpdate {
	@Autowired
	private UnitDaoService unitDaoService;
	private final UnitSeenPK unitSeenPK;
	
	public DisableUnitSeenEventUpdate(UnitSeenPK unitSeenPK) {
		this.unitSeenPK = unitSeenPK;
	}

	@Override
	protected void executeWrite() {
		unitDaoService.disable(unitSeenPK);
	}
}
