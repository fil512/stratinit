package com.kenstevens.stratinit.client.server.rest.session;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.cache.GameCache;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class StratInitSessionManager {
	private final Map<Player, StratInitSession> sessionMap = new HashMap<Player, StratInitSession>();
	@Autowired
	private DataCache dataCache;

	public Nation getNation(Player player) {
		return getSession(player).getNation();
	}

	public Nation setNation(Player player, int gameId) {
		StratInitSession session = getSession(player);
		GameCache gameCache = dataCache.getGameCache(gameId);
		Nation nation = null;
		if (gameCache != null) {
			nation = gameCache.getNation(player);
			session.setNation(nation);
		}
		return nation;
	}

	private StratInitSession getSession(Player player) {
		StratInitSession session = sessionMap.get(player);
		if (session == null) {
			session = new StratInitSession();
			sessionMap.put(player, session);
		}
		return session;
	}
}
