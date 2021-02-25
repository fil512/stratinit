package com.kenstevens.stratinit.server.remote.request.write;

import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.remote.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class JoinGameRequest extends PlayerWriteRequest<Nation> {

	private final Player player;
	private final int gameId;
	private final boolean noAlliances;

	public JoinGameRequest(Player player, int gameId, boolean noAlliances) {
		this.player = player;
		this.gameId = gameId;
		this.noAlliances = noAlliances;
	}

	@Override
	protected Result<Nation> executeWrite() {
		Player joiningPlayer = player;
		if (joiningPlayer == null) {
			joiningPlayer = getPlayer();
		} else {
			setContext(joiningPlayer);
		}
		return gameDaoService.joinGame(joiningPlayer, gameId, noAlliances);
	}

	@Override
	protected int getCommandCost() {
		return 0;
	}
}
