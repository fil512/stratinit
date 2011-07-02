package com.kenstevens.stratinit.site.command;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.site.Command;

@Scope("prototype")
@Component
public class JoinGameCommand extends Command<Nation> {
	private final int gameId;

	public JoinGameCommand(int gameId) {
		this.gameId = gameId;
	}

	@Override
	public Result<Nation> execute() {
		return stratInit.joinGame(gameId);
	}

	@Override
	public String getDescription() {
		return "Joining Game #"+gameId;
	}

	@Override
	public void handleSuccess(Nation nation) {
	}
}
