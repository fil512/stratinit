package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.site.Action;
import com.kenstevens.stratinit.client.site.command.NextBuildUnitCommand;
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
