package com.kenstevens.stratinit.dal.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.PlayerRole;

@SuppressWarnings("unchecked")
@Service
public class PlayerDalImpl implements PlayerDal {
	@PersistenceContext
	protected EntityManager entityManager;

	@Override
	public void persist(Player player) {
		entityManager.persist(player);
	}

	@Override
	public void persist (PlayerRole playerRole) 
    { 
        entityManager.persist(playerRole); 
    } 

	@Override
	public List<Player> getAllPlayers() {
		return entityManager.createQuery("from Player").getResultList();
	}

	private Player find(String username) {
		 List<Player> list = entityManager.createQuery("from Player p WHERE p.username = :username")
	      .setParameter("username", username).getResultList();
		 if (list.isEmpty()) {
			 return null;
		 }
		 if (list.size() > 1) {
			 throw new IllegalStateException();
		 }
		 return list.get(0);
	}

	@Override
	public void remove(String username) {
		Player player = find(username);
		if (player == null) {
			return;
		}
		List<PlayerRole> playerRoles = entityManager.createQuery(
				"from PlayerRole r WHERE r.player = :player")
				.setParameter("player", player).getResultList();
		for (PlayerRole playerRole : playerRoles) {
			entityManager.remove(playerRole);
		}
		entityManager.remove(player);
	}

	@Override
	public void remove(Player player) {
		remove(player.getUsername());
	}
	
	@Override
	public PlayerRole getPlayerRole(Player player, String roleName) {
		return (PlayerRole) entityManager.createQuery("from PlayerRole r WHERE r.player = :player and r.roleName = :roleName")
	      .setParameter("player", player).setParameter("roleName", roleName).getSingleResult();
	}

	@Override
	public void merge(Player player) {
		entityManager.merge(player);
	}

	@Override
	public List<PlayerRole> getRoles(Player player) {
		 return entityManager.createQuery("from PlayerRole p WHERE p.player = :player")
	      .setParameter("player", player).getResultList();
	}
}
