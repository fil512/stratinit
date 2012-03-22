package com.kenstevens.stratinit.server.daoservice;

import java.util.Date;
import java.util.List;

import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;

public interface PlayerDaoService {

	public abstract Result<Player> register(Player player);

	public abstract List<Player> getPlayers();

	public abstract Result<Player> updatePlayer(Player player);

	public abstract boolean isAdmin(Player player);

	public abstract Result<None> forgottenPassword(String username, String email);

	public abstract boolean authorizePlayer(String userName, String password);

	public abstract void setLastLogin(Player player, Date now);

	public abstract Player findPlayer(String username);
}