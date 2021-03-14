package com.kenstevens.stratinit.server.rest.request.write;

import com.kenstevens.stratinit.dto.SICityUpdate;
import com.kenstevens.stratinit.remote.CityFieldToUpdateEnum;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.rest.svc.CityUpdater;
import com.kenstevens.stratinit.type.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class UpdateCityRequest extends PlayerWriteRequest<SICityUpdate> {
	private final SICityUpdate sicity;
	private final CityFieldToUpdateEnum field;
	@Autowired
	private CityUpdater cityUpdater;

	public UpdateCityRequest(SICityUpdate sicity, CityFieldToUpdateEnum field) {
		this.sicity = sicity;
		this.field = field;
	}

	@Override
	protected Result<SICityUpdate> executeWrite() {
		return cityUpdater.updateCity(getNation(), sicity, field);
	}

	@Override
	protected int getCommandCost() {
		return Constants.COMMAND_COST;
	}
}
