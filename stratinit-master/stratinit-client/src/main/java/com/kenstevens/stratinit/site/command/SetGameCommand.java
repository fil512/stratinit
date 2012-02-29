package com.kenstevens.stratinit.site.command;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.site.Command;

@Scope("prototype")
@Component
public class SetGameCommand extends Command<None> {
	private final int gameId;

	public SetGameCommand(int gameId) {
		this.gameId = gameId;
	}

	@Override
	public Result<None> execute() {
		return stratInit.setGame(gameId);
	}

	@Override
	public String getDescription() {
		return "Switch to Game #"+gameId;
	}

	@Override
	public void handleSuccess(None none) {
	}
}
