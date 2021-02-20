package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.GetUpdateCommand;
import com.kenstevens.stratinit.util.Spring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Scope("prototype")
@Component
public class GetUpdateAction extends Action {
	@Autowired
	private Spring spring;
	private GetUpdateCommand getUpdateCommand;
	private final boolean firstTime;

	public GetUpdateAction(boolean firstTime) {
		this.firstTime = firstTime;
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		getUpdateCommand = spring.autowire(new GetUpdateCommand(firstTime));
	}

	@Override
	public Command<? extends Object> getCommand() {
		return getUpdateCommand;
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

}