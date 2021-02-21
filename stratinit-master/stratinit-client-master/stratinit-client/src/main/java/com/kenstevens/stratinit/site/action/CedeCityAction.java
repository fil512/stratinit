package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.NationView;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.CedeCityCommand;
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