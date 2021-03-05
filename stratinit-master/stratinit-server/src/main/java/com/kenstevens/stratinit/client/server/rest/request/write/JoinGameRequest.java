package com.kenstevens.stratinit.client.server.rest.request.write;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.client.server.rest.svc.NationSvc;
import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.remote.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class JoinGameRequest extends PlayerWriteRequest<SINation> {
	@Autowired
	NationSvc nationSvc;
	private final Player player;
	private final int gameId;
	private final boolean noAlliances;

	public JoinGameRequest(Player player, int gameId, boolean noAlliances) {
		this.player = player;
		this.gameId = gameId;
		this.noAlliances = noAlliances;
	}

	@Override
	protected Result<SINation> executeWrite() {
		Player joiningPlayer = player;
		if (joiningPlayer == null) {
			joiningPlayer = getPlayer();
		} else {
			setContext(joiningPlayer);
		}
		Result<Nation> result = gameDaoService.joinGame(joiningPlayer, gameId, noAlliances);
		Nation nation = result.getValue();
		SINation siNation = nationSvc.nationToSINation(nation, nation, false, false);
		return new Result<>(result, siNation);
	}

	@Override
	protected int getCommandCost() {
		return 0;
	}
}
