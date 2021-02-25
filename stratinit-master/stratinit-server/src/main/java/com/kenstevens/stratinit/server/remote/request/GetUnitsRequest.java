package com.kenstevens.stratinit.server.remote.request;

import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.server.remote.helper.PlayerUnitList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetUnitsRequest extends PlayerRequest<List<SIUnit>> {
	@Autowired
	private PlayerUnitList playerUnitList;

	@Override
	protected List<SIUnit> execute() {
		return playerUnitList.getUnits(getNation());
	}
}
