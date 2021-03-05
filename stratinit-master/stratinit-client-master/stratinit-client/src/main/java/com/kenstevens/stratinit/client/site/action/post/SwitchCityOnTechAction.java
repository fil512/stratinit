package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.site.Action;
import com.kenstevens.stratinit.client.site.command.SwitchCityOnTechCommand;
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