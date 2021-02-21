package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.GetNationsCommand;
import com.kenstevens.stratinit.util.Spring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetNationsAction extends Action<GetNationsCommand> {
	@Autowired
	private Spring spring;

	protected GetNationsCommand buildCommand() {
		return new GetNationsCommand();
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

}