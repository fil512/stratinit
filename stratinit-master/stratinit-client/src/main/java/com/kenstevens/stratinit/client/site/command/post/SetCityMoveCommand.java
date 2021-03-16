package com.kenstevens.stratinit.client.site.command.post;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.site.PostCommand;
import com.kenstevens.stratinit.client.site.processor.CityProcessor;
import com.kenstevens.stratinit.dto.SICityUpdate;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.UpdateCityJson;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SetCityMoveCommand extends PostCommand<SICityUpdate, UpdateCityJson> {
	@Autowired
	private CityProcessor cityProcessor;

	public SetCityMoveCommand(City city, SectorCoords nextCoords) {
		super(new UpdateCityJson(new SICityUpdate(city, nextCoords)), getDescription(city));
	}

	@Override
	public Result<SICityUpdate> executePost(UpdateCityJson request) {
		return stratInitServer.updateCity(request);
	}

	public static String getDescription(City city) {
		return "Update " + city;
	}

	@Override
	public void handleSuccess(SICityUpdate sicity) {
		cityProcessor.process(sicity);
	}
}
