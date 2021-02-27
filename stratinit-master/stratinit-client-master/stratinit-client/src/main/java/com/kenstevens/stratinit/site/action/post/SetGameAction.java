package com.kenstevens.stratinit.site.action.post;

import com.kenstevens.stratinit.remote.request.SetGameRequest;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.SetGameCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
// FIXME parameterize by request type
public class SetGameAction extends Action<SetGameCommand> {
	private final SetGameRequest request;

	public SetGameAction(SetGameRequest request) {
		this.request = request;
	}

	protected SetGameCommand buildCommand() {
		return new SetGameCommand(request);
	}
}