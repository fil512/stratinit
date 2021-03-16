package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.site.PostAction;
import com.kenstevens.stratinit.client.site.command.get.SwitchCityOnTechCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SwitchCityOnTechAction extends PostAction<SwitchCityOnTechCommand> {
	public SwitchCityOnTechAction(City city) {
		super(new SwitchCityOnTechCommand(city));
	}
}