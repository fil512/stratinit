package com.kenstevens.stratinit.site.action;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.event.UnitListReplacementArrivedEvent;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.GetUnitsCommand;
import com.kenstevens.stratinit.ui.shell.StatusReporter;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class UnitListAction extends Action {
	@Autowired
	private Spring spring;
	@Autowired
	private Data db;
	@Autowired
	private StatusReporter statusReporter;
	@Autowired
	private GetUnitsCommand getUnitsCommand;

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		getUnitsCommand = spring.getBean(GetUnitsCommand.class);
	}

	@Override
	public Command<? extends Object> getCommand() {
		return getUnitsCommand;
	}

	@Override
	public void postRequest() {
		super.postRequest();
		statusReporter.reportResult(db.getUnitList().size()+" units loaded.");
		arrivedDataEventAccumulator.addEvent(new UnitListReplacementArrivedEvent());
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

}