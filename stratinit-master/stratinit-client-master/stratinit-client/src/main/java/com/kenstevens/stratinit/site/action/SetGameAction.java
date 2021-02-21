package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.SetGameCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SetGameAction extends Action<SetGameCommand> {
	private final int gameId;
	private final boolean noAlliances;

	public SetGameAction(Integer gameId, boolean noAlliances) {
		this.gameId = gameId;
		this.noAlliances = noAlliances;
	}

	protected SetGameCommand buildCommand() {
		return new SetGameCommand(gameId, noAlliances);
	}
}