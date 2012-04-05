package com.kenstevens.stratinit.server.daoserviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.LogDao;
import com.kenstevens.stratinit.model.AttackType;
import com.kenstevens.stratinit.model.BattleLog;
import com.kenstevens.stratinit.model.ErrorLog;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.server.daoservice.LogDaoService;

@Service
public class LogDaoServiceImpl implements LogDaoService {

	@Autowired
	private LogDao logDao;
	@Autowired
	private GameDao gameDao;
	
	public void persist(BattleLog battleLog) {
		logDao.persist(battleLog);
		Nation defender = battleLog.getDefender();
		if (defender != null && battleLog.getAttackType() == AttackType.INITIAL_ATTACK) {
			defender.setNewBattle(true);
			gameDao.merge(defender);
		}
	}

	public void removeLogs(Game game) {
		logDao.removeLogs(game);
	}

	@Override
	public int logError(Game game, Player player, String stackTrace) {
		ErrorLog errorLog = new ErrorLog(game == null ? -1 : game.getId(), player == null ? "NO_PLAYER" : player.getUsername(), stackTrace);
		logDao.persist(errorLog);
		return errorLog.getId();
	}
	
}
