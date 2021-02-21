package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.JoinGameCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class JoinGameAction extends Action<JoinGameCommand> {
	private final int gameId;
	private final boolean noAlliances;

	public JoinGameAction(Integer gameId, boolean noAlliances) {
		this.gameId = gameId;
		this.noAlliances = noAlliances;
	}

	protected JoinGameCommand buildCommand() {
		return new JoinGameCommand(gameId, noAlliances);
	}
}