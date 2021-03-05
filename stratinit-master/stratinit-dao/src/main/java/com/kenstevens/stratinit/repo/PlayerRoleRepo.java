package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.client.model.PlayerRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRoleRepo extends JpaRepository<PlayerRole, Integer> {
    PlayerRole findByPlayerAndRoleName(Player player, String roleName);

    List<PlayerRole> findByPlayer(Player player);

    void deleteByPlayerUsername(String username);

    void deleteByPlayer(Player player);
}
