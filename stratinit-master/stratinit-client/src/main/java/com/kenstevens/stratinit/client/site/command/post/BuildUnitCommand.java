package com.kenstevens.stratinit.client.site.command.post;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.remote.CityFieldToUpdateEnum;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class BuildUnitCommand extends BuildCommand {

	public BuildUnitCommand(City city, UnitType unitType) {
		super(city, unitType, CityFieldToUpdateEnum.BUILD, buildDescription(city, unitType));
	}

	public static String buildDescription(City city, UnitType unitType) {
		String buildString = unitType == null ? "BASE" : unitType.toString().toLowerCase();

		return "Build " + buildString + " in " + city;
	}

	// FIXME need this?
//	@Override
//	protected void setBuild(SICity sicity) {
//		sicity.build = unitType;
//	}
}
