package com.kenstevens.stratinit.site.action;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.ActionQueue;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.BuildUnitCommand;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.ui.selection.SelectEvent;
import com.kenstevens.stratinit.ui.selection.Selection.Source;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class BuildUnitAction extends Action {
	private final City city;
	private final UnitType choice;
	private BuildUnitCommand buildUnitCommand;
	@Autowired
	private Spring spring;
	
	public BuildUnitAction(City city, UnitType choice) {
		this.city = city;
		this.choice = choice;
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		buildUnitCommand = spring.autowire(new BuildUnitCommand(city, choice));
	}

	@Override
	public Command<? extends Object> getCommand() {
		return buildUnitCommand;
	}
}
