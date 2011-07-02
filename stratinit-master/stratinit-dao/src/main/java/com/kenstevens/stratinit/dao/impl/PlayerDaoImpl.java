package com.kenstevens.stratinit.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.dal.impl.PlayerDal;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.PlayerRole;

@Service
public class PlayerDaoImpl implements PlayerDao {
	@Autowired
	private PlayerDal playerDal;
	@Autowired
	private GameDao gameDao;
	@Autowired
	protected DataCache dataCache;

	@Override
	public List<Player> getAllPlayers() {
		return dataCache.getAllPlayers();
	}

	@Override
	public void persist(Player player) {
		playerDal.persist(player);
		dataCache.add(player);
	}

	@Override
	public void persist(PlayerRole playerRole) {
		playerDal.persist(playerRole);
	}

	@Override
	public Player find(String username) {
		if (username == null) {
			return null;
		}
		for (Player player : dataCache.getAllPlayers()) {
			if (username.equals(player.getUsername())) {
				return player;
			}
		}
		return null;
	}

	@Override
	public void remove(String username) {
		playerDal.remove(username);
	}

	@Override
	public void remove(Player player) {
		playerDal.remove(player);
		dataCache.remove(player);
	}

	@Override
	public PlayerRole getPlayerRole(Player player, String roleName) {
		return playerDal.getPlayerRole(player, roleName);
	}

	@Override
	public void merge(Player player) {
		playerDal.merge(player);
		// Note this behavior is different from other daos.  We don't mind doing a db merge every time
		// since player updates are rare.
		List<Nation> nations = gameDao.getNations(player);
		for (Nation nation : nations) {
			nation.getPlayer().copyFrom(player);
		}
	}

	@Override
	public List<PlayerRole> getRoles(Player player) {
		return playerDal.getRoles(player);
	}

	@Override
	public Player findByEmail(String email) {
		if (email == null) {
			return null;
		}
		for (Player player : dataCache.getAllPlayers()) {
			if (email.equals(player.getEmail())) {
				return player;
			}
		}
		return null;
	}

	@Override
	public Player find(Integer id) {
		return dataCache.getPlayer(id);
	}

}
