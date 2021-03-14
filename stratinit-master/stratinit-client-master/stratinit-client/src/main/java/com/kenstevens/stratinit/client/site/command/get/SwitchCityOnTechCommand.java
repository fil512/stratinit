package com.kenstevens.stratinit.client.site.command.get;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.site.PostCommand;
import com.kenstevens.stratinit.client.site.processor.CityProcessor;
import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.remote.CityFieldToUpdateEnum;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.UpdateCityJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SwitchCityOnTechCommand extends PostCommand<SICity, UpdateCityJson> {
	@Autowired
	private CityProcessor cityProcessor;

	public SwitchCityOnTechCommand(City city) {
		super(new UpdateCityJson(new SICity(city), CityFieldToUpdateEnum.SWITCH_ON_TECH_CHANGE), buildDescription(city));
	}

	@Override
	public Result<SICity> executePost(UpdateCityJson request) {
		return stratInitServer.updateCity(request);
	}

	public static String buildDescription(City city) {
		return "Update " + city;
	}


	@Override
	public void handleSuccess(SICity sicity) {
		cityProcessor.process(sicity);
	}
}
