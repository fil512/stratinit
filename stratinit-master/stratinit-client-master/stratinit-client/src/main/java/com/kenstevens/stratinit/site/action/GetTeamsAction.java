package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.GetTeamsCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetTeamsAction extends Action<GetTeamsCommand> {
	protected GetTeamsCommand buildCommand() {
		return new GetTeamsCommand();
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

}