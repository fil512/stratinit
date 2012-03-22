package com.kenstevens.stratinit.site.action;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.SwitchTerrainCommand;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class SwitchTerrainAction extends Action {
	@Autowired
	private Spring spring;
	private SwitchTerrainCommand switchTerrainCommand;

	private final List<UnitView> units;

	public SwitchTerrainAction(List<UnitView> units) {
		this.units = units;
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		switchTerrainCommand = spring.autowire(new SwitchTerrainCommand(units));
	}

	@Override
	public Command<? extends Object> getCommand() {
		return switchTerrainCommand;
	}
}