package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.site.Action;
import com.kenstevens.stratinit.client.site.command.BuildUnitCommand;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
// FIXME continue converting these to PostAction
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
