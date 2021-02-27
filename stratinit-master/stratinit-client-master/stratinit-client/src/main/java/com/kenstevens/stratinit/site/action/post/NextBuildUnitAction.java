package com.kenstevens.stratinit.site.action.post;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.NextBuildUnitCommand;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class NextBuildUnitAction extends Action<NextBuildUnitCommand> {
	private final City city;
	private final UnitType choice;

	public NextBuildUnitAction(City city, UnitType choice) {
		this.city = city;
		this.choice = choice;
	}

	protected NextBuildUnitCommand buildCommand() {
		return new NextBuildUnitCommand(city, choice);
	}
}
