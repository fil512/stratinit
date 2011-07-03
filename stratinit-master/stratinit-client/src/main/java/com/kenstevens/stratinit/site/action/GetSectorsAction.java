package com.kenstevens.stratinit.site.action;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.GetSectorsCommand;
import com.kenstevens.stratinit.ui.shell.StatusReporter;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class GetSectorsAction extends Action {
	@Autowired
	private Spring spring;
	@Autowired
	private StatusReporter statusReporter;
	private GetSectorsCommand getSectorsCommand;

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		getSectorsCommand = spring.getBean(GetSectorsCommand.class);
	}

	@Override
	public Command<? extends Object> getCommand() {
		return getSectorsCommand;
	}

	@Override
	public void postRequest() {
		statusReporter.reportResult("Map updated.");
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

}