package com.kenstevens.stratinit.dal.impl;

import java.util.List;

import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.PlayerRole;

public interface PlayerDal {

	void persist(Player player);

	void persist(PlayerRole playerRole);

	List<Player> getAllPlayers();

	void remove(String username);

	void remove(Player player);

	PlayerRole getPlayerRole(Player player, String roleName);

	void merge(Player player);

	List<PlayerRole> getRoles(Player player);
}
