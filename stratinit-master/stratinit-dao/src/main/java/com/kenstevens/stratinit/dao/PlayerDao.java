package com.kenstevens.stratinit.dao;

import java.util.List;

import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.PlayerRole;

public interface PlayerDao {

	public abstract void persist(Player player);

	public abstract void persist(PlayerRole playerRole);

	public abstract List<Player> getAllPlayers();

	public abstract Player find(String username);

	public abstract void remove(String username);

	public abstract void remove(Player player);

	public abstract PlayerRole getPlayerRole(Player player, String roleName);

	public abstract void merge(Player player);

	public abstract List<PlayerRole> getRoles(Player player);

	public abstract Player findByEmail(String email);

	public abstract Player find(Integer id);

}