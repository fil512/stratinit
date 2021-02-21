package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.SwitchCityOnTechCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SwitchCityOnTechAction extends Action<SwitchCityOnTechCommand> {
	private final City city;

	public SwitchCityOnTechAction(City city) {
		this.city = city;
	}

	protected SwitchCityOnTechCommand buildCommand() {
		return new SwitchCityOnTechCommand(city);
	}
}