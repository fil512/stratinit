package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.BuildUnitCommand;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class BuildUnitAction extends Action<BuildUnitCommand> {
	private final City city;
	private final UnitType choice;

	public BuildUnitAction(City city, UnitType choice) {
		this.city = city;
		this.choice = choice;
	}

	protected BuildUnitCommand buildCommand() {
		return new BuildUnitCommand(city, choice);
	}
}
