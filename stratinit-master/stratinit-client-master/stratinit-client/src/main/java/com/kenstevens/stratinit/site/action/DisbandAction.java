package com.kenstevens.stratinit.site.action;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.DisbandCommand;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class DisbandAction extends Action {
	@Autowired
	private Spring spring;
	private DisbandCommand disbandCommand;

	private final List<UnitView> units;

	public DisbandAction(List<UnitView> units) {
		this.units = units;
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		disbandCommand = spring.autowire(new DisbandCommand(units));
	}

	@Override
	public Command<? extends Object> getCommand() {
		return disbandCommand;
	}
}