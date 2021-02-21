package com.kenstevens.stratinit.server.event.update;

import com.kenstevens.stratinit.model.UnitSeenPK;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
