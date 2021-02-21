package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.GetGamesCommand;
import com.kenstevens.stratinit.util.Spring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetGamesAction extends Action<GetGamesCommand> {
	@Autowired
	private Spring spring;

	protected GetGamesCommand buildCommand() {
		return new GetGamesCommand();
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

}