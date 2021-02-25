package com.kenstevens.stratinit.server.daoserviceimpl;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.LogDao;
import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.server.daoservice.LogDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogDaoServiceImpl implements LogDaoService {

	@Autowired
	private LogDao logDao;
	@Autowired
	private GameDao gameDao;

	public void save(BattleLog battleLog) {
		logDao.save(battleLog);
		Nation defender = battleLog.getDefender();
		if (defender != null && battleLog.getAttackType() == AttackType.INITIAL_ATTACK) {
			defender.setNewBattle(true);
			gameDao.markCacheModified(defender);
		}
	}

	public void removeLogs(Game game) {
		logDao.removeLogs(game);
	}

	@Override
	public int logError(Game game, Player player, String stackTrace) {
		ErrorLog errorLog = new ErrorLog(game == null ? -1 : game.getId(), player == null ? "NO_PLAYER" : player.getUsername(), stackTrace);
		logDao.save(errorLog);
		return errorLog.getId();
	}

}
