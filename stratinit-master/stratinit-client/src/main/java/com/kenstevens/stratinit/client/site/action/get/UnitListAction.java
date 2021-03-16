package com.kenstevens.stratinit.client.site.action.get;

import com.kenstevens.stratinit.client.api.IStatusReporter;
import com.kenstevens.stratinit.client.event.UnitListReplacementArrivedEvent;
import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.site.GetAction;
import com.kenstevens.stratinit.client.site.command.get.GetUnitsCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class UnitListAction extends GetAction<GetUnitsCommand> {
	@Autowired
	private Data db;
	@Autowired
	private IStatusReporter statusReporter;

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