package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.NationView;
import com.kenstevens.stratinit.client.site.Action;
import com.kenstevens.stratinit.client.site.command.CedeCityCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class CedeCityAction extends Action<CedeCityCommand> {
	private final City city;
	private final Nation nation;

	public CedeCityAction(City city, NationView nation) {
		this.city = city;
		this.nation = nation;
	}

	protected CedeCityCommand buildCommand() {
		return new CedeCityCommand(city, nation);
	}
}