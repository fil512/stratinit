package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.GetMyNationCommand;
import com.kenstevens.stratinit.util.Spring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetMyNationAction extends Action<GetMyNationCommand> {
	@Autowired
	private Spring spring;

	protected GetMyNationCommand buildCommand() {
		return new GetMyNationCommand();
	}

	@Override
	public boolean canRepeat() {
		return false;
	}
}