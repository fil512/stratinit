package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.SetGameRequest;
import com.kenstevens.stratinit.site.Command;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SetGameCommand extends Command<None> {
	private final SetGameRequest request;

	public SetGameCommand(SetGameRequest request) {
		this.request = request;
	}

	@Override
	public Result<None> execute() {
		return stratInitServer.setGame(request);
	}

	@Override
	public String getDescription() {
		return "Switch to Game #" + request.gameId;
	}

	@Override
	public void handleSuccess(None none) {
	}
}
