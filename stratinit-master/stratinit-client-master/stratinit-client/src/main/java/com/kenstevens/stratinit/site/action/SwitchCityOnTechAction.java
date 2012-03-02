package com.kenstevens.stratinit.site.action;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.SwitchCityOnTechCommand;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class SwitchCityOnTechAction extends Action {
	@Autowired
	private Spring spring;
	private SwitchCityOnTechCommand switchCityOnTechCommand;

	private final City city;

	public SwitchCityOnTechAction(City city) {
		this.city = city;
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		switchCityOnTechCommand = spring.autowire(new SwitchCityOnTechCommand(
					city ));
	}

	@Override
	public Command<? extends Object> getCommand() {
		return switchCityOnTechCommand;
	}
}