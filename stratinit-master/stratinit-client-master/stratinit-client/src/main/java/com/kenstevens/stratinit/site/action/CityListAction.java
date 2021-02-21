package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.event.CityListReplacementArrivedEvent;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.shell.StatusReporter;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.GetCitiesCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class CityListAction extends Action<GetCitiesCommand> {
	@Autowired
	private Data db;
	@Autowired
	private StatusReporter statusReporter;

	protected GetCitiesCommand buildCommand() {
		return new GetCitiesCommand();
	}

	@Override
	public void postRequest() {
		super.postRequest();
		statusReporter.reportResult(db.getCityList().size() + " cities loaded.");
		arrivedDataEventAccumulator.addEvent(new CityListReplacementArrivedEvent());
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

}