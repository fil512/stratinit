package com.kenstevens.stratinit.client.server.rest.request;

import com.kenstevens.stratinit.client.server.rest.svc.UnitSvc;
import com.kenstevens.stratinit.dto.SIUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetUnitsRequest extends PlayerRequest<List<SIUnit>> {
	@Autowired
	private UnitSvc unitSvc;

	@Override
	protected List<SIUnit> execute() {
		return unitSvc.getUnits(getNation());
	}
}
