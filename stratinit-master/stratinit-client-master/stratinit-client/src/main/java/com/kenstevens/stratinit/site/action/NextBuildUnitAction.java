package com.kenstevens.stratinit.site.action;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.NextBuildUnitCommand;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class NextBuildUnitAction extends Action {
	private final City city;
	private final UnitType choice;
	private NextBuildUnitCommand nextBuildUnitCommand;
	@Autowired
	private Spring spring;
	
	public NextBuildUnitAction(City city, UnitType choice) {
		this.city = city;
		this.choice = choice;
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		nextBuildUnitCommand = spring.autowire(new NextBuildUnitCommand(city, choice));
	}

	@Override
	public Command<? extends Object> getCommand() {
		return nextBuildUnitCommand;
	}
}
