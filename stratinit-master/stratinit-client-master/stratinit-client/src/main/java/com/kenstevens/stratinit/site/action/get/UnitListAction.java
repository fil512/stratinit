package com.kenstevens.stratinit.site.action.get;

import com.kenstevens.stratinit.event.UnitListReplacementArrivedEvent;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.shell.StatusReporter;
import com.kenstevens.stratinit.site.GetAction;
import com.kenstevens.stratinit.site.command.GetUnitsCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class UnitListAction extends GetAction<GetUnitsCommand> {
	@Autowired
	private Data db;
	@Autowired
	private StatusReporter statusReporter;

	protected UnitListAction() {
		super(new GetUnitsCommand());
	}

	@Override
	public void postRequest() {
		super.postRequest();
		statusReporter.reportResult(db.getUnitList().size() + " units loaded.");
		arrivedDataEventAccumulator.addEvent(new UnitListReplacementArrivedEvent());
	}
}