package com.kenstevens.stratinit.dao.impl;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.dal.PlayerDal;
import com.kenstevens.stratinit.dal.PlayerRoleDal;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.PlayerRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerDaoImpl implements PlayerDao {
	@Autowired
	private PlayerDal playerDal;
	@Autowired
	private PlayerRoleDal playerRoleDal;
	@Autowired
	private GameDao gameDao;
	@Autowired
	protected DataCache dataCache;

	@Override
	public List<Player> getAllPlayers() {
		return dataCache.getAllPlayers();
	}

	@Override
	public void save(Player player) {
		playerDal.save(player);
		dataCache.add(player);
	}

	@Override
	public void save(PlayerRole playerRole) {
		playerRoleDal.save(playerRole);
	}

	@Override
	public Player find(String username) {
		return playerDal.findByUsername(username);
	}

	@Override
	public void remove(String username) {
		playerDal.deleteByUsername(username);
	}

	@Override
	public void remove(Player player) {
		playerDal.delete(player);
		dataCache.remove(player);
	}

	@Override
	public PlayerRole getPlayerRole(Player player, String roleName) {
		return playerRoleDal.findByPlayerAndRoleName(player, roleName);
	}

	@Override
	public void merge(Player player) {
		playerDal.save(player);
		// Note this behavior is different from other daos.  We don't mind doing a db merge every time
		// since player updates are rare.
		List<Nation> nations = gameDao.getNations(player);
		for (Nation nation : nations) {
			nation.getPlayer().copyFrom(player);
		}
	}

	@Override
	public List<PlayerRole> getRoles(Player player) {
		return playerRoleDal.findByPlayer(player);
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
