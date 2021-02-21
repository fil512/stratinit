package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.event.UnitListReplacementArrivedEvent;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.shell.StatusReporter;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.GetUnitsCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class UnitListAction extends Action<GetUnitsCommand> {
	@Autowired
	private Data db;
	@Autowired
	private StatusReporter statusReporter;

	protected GetUnitsCommand buildCommand() {
		return new GetUnitsCommand();
	}

	@Override
	public void postRequest() {
		super.postRequest();
		statusReporter.reportResult(db.getUnitList().size() + " units loaded.");
		arrivedDataEventAccumulator.addEvent(new UnitListReplacementArrivedEvent());
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

}