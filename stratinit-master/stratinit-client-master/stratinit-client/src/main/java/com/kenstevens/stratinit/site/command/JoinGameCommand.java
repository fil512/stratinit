package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.remote.SIResponseEntity;
import com.kenstevens.stratinit.site.Command;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class JoinGameCommand extends Command<Nation> {
	private final int gameId;
	private final boolean noAlliances;

	public JoinGameCommand(int gameId, boolean noAlliances) {
		this.gameId = gameId;
		this.noAlliances = noAlliances;
	}

	@Override
	public SIResponseEntity<Nation> execute() {
		return stratInit.joinGame(gameId, noAlliances);
	}

	@Override
	public String getDescription() {
		return "Joining Game #"+gameId;
	}

	@Override
	public void handleSuccess(Nation nation) {
	}
}
