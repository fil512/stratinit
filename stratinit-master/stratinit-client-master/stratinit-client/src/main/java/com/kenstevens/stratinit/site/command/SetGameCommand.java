package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.site.Command;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SetGameCommand extends Command<None> {
	private final int gameId;
	private final boolean noAlliances;

	public SetGameCommand(int gameId, boolean noAlliances) {
		this.gameId = gameId;
		this.noAlliances = noAlliances;
	}

	@Override
	public Result<None> execute() {
		return stratInit.setGame(gameId, noAlliances);
	}

	@Override
	public String getDescription() {
		return "Switch to Game #"+gameId;
	}

	@Override
	public void handleSuccess(None none) {
	}
}
