package com.kenstevens.stratinit.site.action;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.event.CityListReplacementArrivedEvent;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.shell.StatusReporter;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.GetCitiesCommand;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class CityListAction extends Action {
	@Autowired
	private Spring spring;
	@Autowired
	private Data db;
	@Autowired
	private StatusReporter statusReporter;
	private GetCitiesCommand getCitiesCommand;

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		getCitiesCommand = spring.getBean(GetCitiesCommand.class);
	}

	@Override
	public Command<? extends Object> getCommand() {
		return getCitiesCommand;
	}

	@Override
	public void postRequest() {
		super.postRequest();
		statusReporter.reportResult(db.getCityList().size()+" cities loaded.");
		arrivedDataEventAccumulator.addEvent(new CityListReplacementArrivedEvent());
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

}