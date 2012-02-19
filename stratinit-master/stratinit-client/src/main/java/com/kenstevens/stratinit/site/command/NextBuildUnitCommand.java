package com.kenstevens.stratinit.site.command;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.remote.UpdateCityField;
import com.kenstevens.stratinit.type.UnitType;

@Scope("prototype")
@Component
public class NextBuildUnitCommand extends BuildCommand {
	public NextBuildUnitCommand(City city, UnitType unitType) {
		super(city, unitType, UpdateCityField.NEXT_BUILD);
	}

	@Override
	public void setBuild(SICity sicity) {
		sicity.nextBuild = unitType;
	}

	@Override
	public String getDescription() {
		String nextBuildString = unitType == null ? "(no change)" : unitType.toString().toLowerCase();
		return "Next build " + nextBuildString + " in " + city;
	}

}
