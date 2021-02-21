package com.kenstevens.stratinit.server.event;

import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.server.event.svc.StratInitUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Scope("prototype")
@Component
public class UnitUpdateEvent extends Event {
	@Autowired
	private StratInitUpdater stratInitUpdater;
	@Autowired
	private UnitDao unitDao;

	UnitUpdateEvent(Unit unit) {
		super(unit);
	}

	@Override
	protected void execute() {
		updateUnit();
	}

	private void updateUnit() {
		Integer unitId = (Integer)getEventKey().getKey();
		Unit unit = unitDao.findUnit(unitId);
		if (unit == null) {
			cancel();
			return;
		}
		stratInitUpdater.updateUnit(unit, new Date());
	}
}
