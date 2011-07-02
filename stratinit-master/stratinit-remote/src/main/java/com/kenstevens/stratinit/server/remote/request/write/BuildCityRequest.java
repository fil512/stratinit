package com.kenstevens.stratinit.server.remote.request.write;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.type.Constants;

@Scope("prototype")
@Component
public class BuildCityRequest extends BuildRequest {

	public BuildCityRequest(List<SIUnit> siunits) {
		super(siunits);
	}

	@Override
	protected int mobilityCost() {
		return Constants.MOB_COST_TO_CREATE_CITY;
	}


	@Override
	protected int getCommandCost() {
		return Constants.COMMAND_COST_BUILD_CITY;
	}
	
	@Override
	protected Result<None> buildIt(Unit unit) {
		return unitDaoService.buildCity(unit);
	}

}
