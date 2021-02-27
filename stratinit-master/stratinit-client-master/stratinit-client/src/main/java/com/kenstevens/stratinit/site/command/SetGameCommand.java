package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.SetGameJson;
import com.kenstevens.stratinit.site.PostCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
// FIXME Post Commands should know their input and output types
public class SetGameCommand extends PostCommand<None, SetGameJson> {
	public SetGameCommand(SetGameJson request) {
		super(request);
	}

	@Override
	public Result<None> execute() {
		return stratInitServer.setGame(getRequest());
	}

	@Override
	public String getDescription() {
		return "Switch to Game #" + getRequest().gameId;
	}

	@Override
	public void handleSuccess(None none) {
	}
}
