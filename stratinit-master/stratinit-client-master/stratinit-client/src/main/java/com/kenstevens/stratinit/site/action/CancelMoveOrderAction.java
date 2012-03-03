package com.kenstevens.stratinit.site.action;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.CancelMoveOrderCommand;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class CancelMoveOrderAction extends Action {
	@Autowired
	private Spring spring;
	private CancelMoveOrderCommand cancelMoveCommand;

	private final List<UnitView> units;

	public CancelMoveOrderAction(List<UnitView> units) {
		this.units = units;
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		cancelMoveCommand = spring.autowire(new CancelMoveOrderCommand(units));
	}

	@Override
	public Command<? extends Object> getCommand() {
		return cancelMoveCommand;
	}
}