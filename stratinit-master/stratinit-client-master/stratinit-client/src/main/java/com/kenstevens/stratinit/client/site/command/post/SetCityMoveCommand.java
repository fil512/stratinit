package com.kenstevens.stratinit.client.site.command.post;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.site.PostCommand;
import com.kenstevens.stratinit.client.site.processor.CityProcessor;
import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.remote.CityFieldToUpdateEnum;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.UpdateCityJson;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SetCityMoveCommand extends PostCommand<SICity, UpdateCityJson> {
	@Autowired
	private CityProcessor cityProcessor;
	
	protected final City city;
	private final SectorCoords coords;

	public SetCityMoveCommand(City city, SectorCoords coords) {
		super(new UpdateCityJson(new SICity(city), CityFieldToUpdateEnum.NEXT_COORDS), getDescription(city));
		this.city = city;
		this.coords = coords;
	}

	@Override
	public Result<SICity> executePost(UpdateCityJson request) {
		return stratInitServer.updateCity(request);
	}

	public static String getDescription(City city) {
		return "Update " + city;
	}

	@Override
	public void handleSuccess(SICity sicity) {
		cityProcessor.process(sicity);
	}
}
