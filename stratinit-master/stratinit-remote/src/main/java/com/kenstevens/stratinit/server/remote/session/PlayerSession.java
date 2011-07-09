package com.kenstevens.stratinit.server.remote.session;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;

@Scope("prototype")
@Component
public class PlayerSession {
	@Autowired
	private PlayerDao playerDao;
	@Autowired
	private ThreadLocalContext context;
	@Autowired
	private StratInitSessionManager sessionManager;
	@Autowired
	private GameDao gameDao;
	
	private Player player;
	private Nation nation;
	private Game game;
	
	@SuppressWarnings("unused")
	@PostConstruct
	private void init() {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		if (authentication == null) {
			return;
		}
		String username = authentication.getName();
		player = playerDao.find(username);
		nation = setContext(player);
	}

	public Nation setContext(Player player) {
		context.initialize(player, sessionManager);
		return context.getNation();
	}

	public Game getGame() {
		if (game != null) {
			return game;
		}
		if (nation == null) {
			return null;
		}
		return nation.getGame();
	}

	public void setGame(int gameId) {
		game = gameDao.findGame(gameId);
	}

	public Nation getNation() {
		return nation;
	}

	public Player getPlayer() {
		return player;
	}


}
