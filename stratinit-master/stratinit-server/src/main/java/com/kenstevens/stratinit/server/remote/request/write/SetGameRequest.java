package com.kenstevens.stratinit.server.remote.request.write;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.cache.GameCache;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.remote.session.StratInitSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SetGameRequest extends PlayerWriteRequest<None> {
	private final int gameId;
	private final boolean noAlliances;
	@Autowired
	private StratInitSessionManager sessionManager;
	@Autowired
	private DataCache dataCache;

	public SetGameRequest(int gameId, boolean noAlliances) {
		this.gameId = gameId;
		this.noAlliances = noAlliances;
	}

	@Override
	protected int getCommandCost() {
		return 0;
	}

	@Override
	protected Result<None> executeWrite() {
		GameCache gameCache = dataCache.getGameCache(gameId);
		if (gameCache == null) {
			return new Result<None>("The game " + gameId + " does not exist.", false);
		}
		Game game = gameCache.getGame();
		if (game == null) {
			return new Result<None>("The game " + gameId + " does not exist.", false);
		}
		if (game.hasEnded()) {
			return new Result<None>("The game " + gameId + " has ended.", false);
		}
		if (!game.isMapped()) {
			return new Result<None>("The game " + gameId + " is not open yet.", false);
		}
		Nation nation = sessionManager.setNation(getPlayer(), gameId);
		if (nation == null || dataCache.getGameCache(nation.getGame()) == null) {
			return new Result<None>("You have not joined game #" + gameId + " (This error should never happen!)", false);
		}
		if (!game.hasStarted()) {
			nation.setNoAlliances(noAlliances);
			gameDaoService.merge(nation);
			gameDaoService.calculateAllianceVote(game);
			gameDaoService.merge(game);
		}
		return Result.trueInstance();
	}

	@Override
	protected void setLastAction() {
		// Don't want to set our last action time for set game
	}
}
