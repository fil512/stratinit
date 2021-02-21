package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.GetNewsCommand;
import com.kenstevens.stratinit.util.Spring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetNewsAction extends Action<GetNewsCommand> {
	@Autowired
	private Spring spring;

	protected GetNewsCommand buildCommand() {
		return new GetNewsCommand();
	}

	@Override
	public boolean canRepeat() {
		return false;
	}
}