package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.remote.request.SetGameRequest;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.JoinGameCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class JoinGameAction extends Action<JoinGameCommand> {

	private final SetGameRequest request;

	public JoinGameAction(SetGameRequest request) {
		this.request = request;
	}

	protected JoinGameCommand buildCommand() {
		return new JoinGameCommand(request);
	}
}