package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.GetUnjoinedGamesCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetUnjoinedGamesAction extends Action<GetUnjoinedGamesCommand> {
	protected GetUnjoinedGamesCommand buildCommand() {
		return new GetUnjoinedGamesCommand();
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

}