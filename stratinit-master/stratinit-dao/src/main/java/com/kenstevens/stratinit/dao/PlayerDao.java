package com.kenstevens.stratinit.dao;

import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.PlayerRole;

import java.util.List;

public interface PlayerDao {

	void save(Player player);

	void save(PlayerRole playerRole);

	List<Player> getAllPlayers();

	Player find(String username);

	void deleteByUsername(String username);

	void delete(Player player);

	PlayerRole getPlayerRole(Player player, String roleName);

	void merge(Player player);

	List<PlayerRole> getRoles(Player player);

	Player findByEmail(String email);

	Player find(Integer id);

}