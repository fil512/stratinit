package com.kenstevens.stratinit.client.site.action.get;

import com.kenstevens.stratinit.client.event.CityListReplacementArrivedEvent;
import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.site.GetAction;
import com.kenstevens.stratinit.client.site.command.get.GetCitiesCommand;
import com.kenstevens.stratinit.shell.StatusReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class CityListAction extends GetAction<GetCitiesCommand> {
	@Autowired
	private Data db;
	@Autowired
	private StatusReporter statusReporter;

	public CityListAction() {
		super(new GetCitiesCommand());
	}

	@Override
	public void postRequest() {
		super.postRequest();
		statusReporter.reportResult(db.getCityList().size() + " cities loaded.");
		arrivedDataEventAccumulator.addEvent(new CityListReplacementArrivedEvent());
	}
}