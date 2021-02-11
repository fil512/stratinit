package com.kenstevens.stratinit.dal;

import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.PlayerRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRoleDal extends JpaRepository<PlayerRole, Integer> {
	PlayerRole findByPlayerAndRoleName(Player player, String roleName);

	List<PlayerRole> findByPlayer(Player player);
}
