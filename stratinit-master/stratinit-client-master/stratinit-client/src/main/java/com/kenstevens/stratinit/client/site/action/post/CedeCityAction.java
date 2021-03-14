package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.NationView;
import com.kenstevens.stratinit.client.site.PostAction;
import com.kenstevens.stratinit.client.site.command.post.CedeCityCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class CedeCityAction extends PostAction<CedeCityCommand> {
	public CedeCityAction(City city, NationView nation) {
		super(new CedeCityCommand(city, nation));
	}
}
