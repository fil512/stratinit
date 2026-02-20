package com.kenstevens.stratinit.dao;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.client.model.PlayerRole;
import com.kenstevens.stratinit.repo.PlayerRepo;
import com.kenstevens.stratinit.repo.PlayerRoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;

@Service
public class PlayerDao {
	@Autowired
	protected DataCache dataCache;
	@Autowired
	private PlayerRepo playerRepo;
	@Autowired
    private PlayerRoleRepo playerRoleRepo;
    @Autowired
    private NationDao nationDao;

	public List<Player> getAllPlayers() {
		return dataCache.getAllPlayers();
	}

	public void save(Player player) {
		playerRepo.save(player);
		dataCache.add(player);
	}

	public void save(PlayerRole playerRole) {
		playerRoleRepo.save(playerRole);
	}

	public Player find(String username) {
		return playerRepo.findByUsername(username);
	}

	@Transactional
	public void deleteByUsername(String username) {
		playerRoleRepo.deleteByPlayerUsername(username);
		playerRepo.deleteByUsername(username);
	}

	public void delete(Player player) {
		playerRoleRepo.deleteByPlayer(player);
		playerRepo.delete(player);
		dataCache.remove(player);
	}

	public PlayerRole getPlayerRole(Player player, String roleName) {
		return playerRoleRepo.findByPlayerAndRoleName(player, roleName);
	}

	public void saveAndUpdateNations(Player player) {
        playerRepo.save(player);
        // Note this behavior is different from other daos.  We don't mind doing a db save every time
        // since player updates are rare.
        List<Nation> nations = nationDao.getNations(player);
        for (Nation nation : nations) {
            nation.getPlayer().copyFrom(player);
        }
    }

	public List<PlayerRole> getRoles(Player player) {
		return playerRoleRepo.findByPlayer(player);
	}

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

	public Player find(Integer id) {
		return dataCache.getPlayer(id);
	}
}
