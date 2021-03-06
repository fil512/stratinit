package com.kenstevens.stratinit.client.site.command;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.site.Command;
import com.kenstevens.stratinit.client.site.processor.CityProcessor;
import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.UpdateCityField;
import com.kenstevens.stratinit.remote.request.UpdateCityJson;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BuildCommand extends Command<SICity> {
	@Autowired
	private CityProcessor cityProcessor;
	
	protected final City city;
	protected final UnitType unitType;
	protected final UpdateCityField field;

	protected BuildCommand(City city, UnitType unitType, UpdateCityField field) {
		this.city = city;
		this.unitType = unitType;
		this.field = field;
	}

	@Override
	public Result<SICity> execute() {
		SICity sicity = new SICity(city);
		setBuild(sicity);
		UpdateCityJson request = new UpdateCityJson(sicity, field);
		return stratInitServer.updateCity(request);
	}

	protected abstract void setBuild(SICity sicity);

	@Override
	public String getDescription() {
		return "Build " + unitType.toString().toLowerCase() + " in " + city;
	}


	@Override
	public void handleSuccess(SICity sicity) {
		cityProcessor.process(sicity);
	}
}
