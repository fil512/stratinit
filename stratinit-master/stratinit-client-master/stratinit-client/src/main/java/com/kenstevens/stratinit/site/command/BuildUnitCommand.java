package com.kenstevens.stratinit.site.command;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.remote.UpdateCityField;
import com.kenstevens.stratinit.type.UnitType;

@Scope("prototype")
@Component
public class BuildUnitCommand extends BuildCommand {

	public BuildUnitCommand(City city, UnitType unitType) {
		super(city, unitType, UpdateCityField.BUILD);
	}

	@Override
	public String getDescription() {
		String buildString = unitType == null ? "BASE" : unitType.toString().toLowerCase();

		return "Build " + buildString + " in " + city;
	}

	@Override
	protected void setBuild(SICity sicity) {
		sicity.build = unitType;
	}
}
