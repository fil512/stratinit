package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.GetUpdateCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetUpdateAction extends Action<GetUpdateCommand> {
	private final boolean firstTime;

	public GetUpdateAction(boolean firstTime) {
		this.firstTime = firstTime;
	}

	protected GetUpdateCommand buildCommand() {
		return new GetUpdateCommand(firstTime);
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

}