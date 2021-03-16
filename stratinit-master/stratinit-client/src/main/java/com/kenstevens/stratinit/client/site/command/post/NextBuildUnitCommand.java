package com.kenstevens.stratinit.client.site.command.post;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.remote.CityFieldToUpdateEnum;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class NextBuildUnitCommand extends BuildCommand {
	public NextBuildUnitCommand(City city, UnitType unitType) {
		super(city, unitType, CityFieldToUpdateEnum.NEXT_BUILD, buildDescription(city, unitType));
	}

	public static String buildDescription(City city, UnitType unitType) {
		String nextBuildString = unitType == null ? "(no change)" : unitType.toString().toLowerCase();
		return "Next build " + nextBuildString + " in " + city;
	}
}
