package com.kenstevens.stratinit.server.remote.session;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.cache.GameCache;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;

@Repository
public class StratInitSessionManager {
	@Autowired
	private DataCache dataCache;
	private final Map<Player, StratInitSession> sessionMap = new HashMap<Player, StratInitSession>();
	
	public Nation getNation(Player player) {
		return getSession(player).getNation();
	}
	
	public void setNation(Player player, int gameId) {
		StratInitSession session = getSession(player);
		GameCache gameCache = dataCache.getGameCache(gameId);
		if (gameCache != null) {
			session.setNation(gameCache.getNation(player));
		}
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
