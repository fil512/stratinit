package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.UpdateCityField;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.processor.CityProcessor;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SetCityMoveCommand extends Command<SICity> {
	@Autowired
	private CityProcessor cityProcessor;
	
	protected final City city;
	private final SectorCoords coords;

	public SetCityMoveCommand(City city, SectorCoords coords) {
		this.city = city;
		this.coords = coords;
	}

	@Override
	public Result<SICity> execute() {
        SICity sicity = new SICity(city);
        sicity.nextCoords = coords;
        return stratInitServer.updateCity(sicity, UpdateCityField.NEXT_COORDS);
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
