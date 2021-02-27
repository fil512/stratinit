package com.kenstevens.stratinit.server.rest.request.write;

import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.UpdateCityField;
import com.kenstevens.stratinit.server.rest.helper.CityUpdater;
import com.kenstevens.stratinit.type.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class UpdateCityRequest extends PlayerWriteRequest<SICity> {
	private final SICity sicity;
	private final UpdateCityField field;
	@Autowired
	private CityUpdater cityUpdater;

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
