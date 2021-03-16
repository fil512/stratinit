package com.kenstevens.stratinit.client.site.action.get;

import com.kenstevens.stratinit.client.api.INotifier;
import com.kenstevens.stratinit.client.event.CityListReplacementArrivedEvent;
import com.kenstevens.stratinit.client.event.UnitListReplacementArrivedEvent;
import com.kenstevens.stratinit.client.site.GetAction;
import com.kenstevens.stratinit.client.site.command.get.DescriptionCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class StartupAction extends GetAction<DescriptionCommand> {
	@Autowired
	private INotifier notifier;

	protected StartupAction() {
		super(new DescriptionCommand("Start up"));
	}

	@Override
	public void postRequest() {
		arrivedDataEventAccumulator.addEvent(new CityListReplacementArrivedEvent());
		arrivedDataEventAccumulator.addEvent(new UnitListReplacementArrivedEvent());
		notifier.finishedStartingUp();
	}
}