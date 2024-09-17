package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.site.PostAction;
import com.kenstevens.stratinit.client.site.command.post.NextBuildUnitCommand;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class NextBuildUnitAction extends PostAction<NextBuildUnitCommand> {
	public NextBuildUnitAction(City city, UnitType choice) {
		super(new NextBuildUnitCommand(city, choice));
	}
}
