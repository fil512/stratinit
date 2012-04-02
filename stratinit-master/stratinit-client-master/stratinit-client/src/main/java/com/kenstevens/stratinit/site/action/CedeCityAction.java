package com.kenstevens.stratinit.site.action;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.NationView;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.CedeCityCommand;
import com.kenstevens.stratinit.site.command.CedeCommand;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class CedeCityAction extends Action {
	@Autowired
	private Spring spring;
	private CedeCommand cedeCommand;

	private final City city;
	private final Nation nation;

	public CedeCityAction(City city, NationView nation) {
		this.city = city;
		this.nation = nation;
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		cedeCommand = spring.autowire(new CedeCityCommand(city, nation));
	}

	@Override
	public Command<? extends Object> getCommand() {
		return cedeCommand;
	}
}