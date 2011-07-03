package com.kenstevens.stratinit.server.remote.request.write;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.remote.Result;

@Scope("prototype")
@Component
public class JoinGameRequest extends PlayerWriteRequest<Nation> {

	private final Player player;
	private final int gameId;

	public JoinGameRequest(Player player, int gameId) {
		this.player = player;
		this.gameId = gameId;
	}

	@Override
	protected Result<Nation> executeWrite() {
		Player joiningPlayer = player;
		if (joiningPlayer == null) {
			joiningPlayer = getPlayer();
		} else {
			setContext(joiningPlayer);
		}
		return gameDaoService.joinGame(joiningPlayer, gameId);
	}

	@Override
	protected int getCommandCost() {
		return 0;
	}
}
