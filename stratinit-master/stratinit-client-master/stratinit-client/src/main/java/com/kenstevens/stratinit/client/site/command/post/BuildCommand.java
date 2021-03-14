package com.kenstevens.stratinit.client.site.command.post;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.site.PostCommand;
import com.kenstevens.stratinit.client.site.processor.CityProcessor;
import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.remote.CityFieldToUpdateEnum;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.UpdateCityJson;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BuildCommand extends PostCommand<SICity, UpdateCityJson> {
	@Autowired
	private CityProcessor cityProcessor;

	protected BuildCommand(City city, UnitType unitType, CityFieldToUpdateEnum field, String description) {
		super(new UpdateCityJson(new SICity(city), unitType, field), description);
	}

	@Override
	public Result<SICity> executePost(UpdateCityJson request) {
		return stratInitServer.updateCity(request);
	}

	@Override
	public void handleSuccess(SICity sicity) {
		cityProcessor.process(sicity);
	}


// FIXME need this?
	//	protected abstract void setBuild(SICity sicity);
}
