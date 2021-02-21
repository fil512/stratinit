package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.GetVersionCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetVersionAction extends Action<GetVersionCommand> {
	protected GetVersionCommand buildCommand() {
		return new GetVersionCommand();
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

}