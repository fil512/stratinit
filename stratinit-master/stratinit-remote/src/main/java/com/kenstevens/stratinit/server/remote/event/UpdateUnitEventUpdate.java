package com.kenstevens.stratinit.server.remote.event;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;

@Scope("prototype")
@Component
public class UpdateUnitEventUpdate extends EventUpdate {
	@Autowired
	private UnitDaoService unitDaoService;
	private final Unit unit;
	private final Date date;
	
	public UpdateUnitEventUpdate(Unit unit, Date date) {
		this.unit = unit;
		this.date = date;
	}

	@Override
	protected void executeWrite() {
		unitDaoService.updateUnit(unit, date);
	}
}
