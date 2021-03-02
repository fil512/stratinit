package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.UpdateCityField;
import com.kenstevens.stratinit.remote.request.UpdateCityJson;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.processor.CityProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SwitchCityOnTechCommand extends Command<SICity> {
	@Autowired
	private CityProcessor cityProcessor;
	
	protected final City city;

	public SwitchCityOnTechCommand(City city) {
		this.city = city;
	}

	@Override
	public Result<SICity> execute() {
		SICity sicity = new SICity(city);
		sicity.switchOnTechChange = city.isSwitchOnTechChange();
		UpdateCityJson request = new UpdateCityJson(sicity, UpdateCityField.SWITCH_ON_TECH_CHANGE);
		return stratInitServer.updateCity(request);
	}

	@Override
	public String getDescription() {
		return "Update " + city;
	}


	@Override
	public void handleSuccess(SICity sicity) {
		cityProcessor.process(sicity);
	}
}
