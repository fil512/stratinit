package com.kenstevens.stratinit.server.event;

import com.kenstevens.stratinit.client.model.UnitSeen;
import com.kenstevens.stratinit.client.model.UnitSeenPK;
import com.kenstevens.stratinit.server.event.svc.StratInitUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class UnitSeenEvent extends Event {
	@Autowired
	private StratInitUpdater stratInitUpdater;

	UnitSeenEvent(UnitSeen unitSeen) {
		super(unitSeen, unitSeen.getExpiry());
	}

	@Override
	protected void execute() {
		UnitSeenPK unitSeenPK = (UnitSeenPK) getEventKey().getKey();
		stratInitUpdater.disable(unitSeenPK);
	}
}
