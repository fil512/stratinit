package com.kenstevens.stratinit.server.remote.request.write;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.UpdateCityField;
import com.kenstevens.stratinit.server.remote.helper.CityUpdater;
import com.kenstevens.stratinit.type.Constants;

@Scope("prototype")
@Component
public class UpdateCityRequest extends PlayerWriteRequest<SICity> {
	@Autowired
	private CityUpdater cityUpdater;

	private final SICity sicity;
	private final UpdateCityField field;

	public UpdateCityRequest(SICity sicity, UpdateCityField field) {
		this.sicity = sicity;
		this.field = field;
	}

	@Override
	protected Result<SICity> executeWrite() {
		return cityUpdater.updateCity(getNation(), sicity, field);
	}

	@Override
	protected int getCommandCost() {
		return Constants.COMMAND_COST;
	}
}
