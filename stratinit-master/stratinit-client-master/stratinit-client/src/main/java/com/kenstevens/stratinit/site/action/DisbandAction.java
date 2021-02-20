package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.DisbandCommand;
import com.kenstevens.stratinit.util.Spring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

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