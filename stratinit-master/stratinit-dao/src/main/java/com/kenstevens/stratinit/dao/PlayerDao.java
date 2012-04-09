package com.kenstevens.stratinit.dao;

import java.util.List;

import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.PlayerRole;

public interface PlayerDao {

	void persist(Player player);

	void persist(PlayerRole playerRole);

	List<Player> getAllPlayers();

	Player find(String username);

	void remove(String username);

	void remove(Player player);

	PlayerRole getPlayerRole(Player player, String roleName);

	void merge(Player player);

	List<PlayerRole> getRoles(Player player);

	Player findByEmail(String email);

	Player find(Integer id);

}