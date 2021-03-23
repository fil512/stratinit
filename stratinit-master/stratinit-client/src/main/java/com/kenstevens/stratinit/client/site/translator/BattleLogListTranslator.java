package com.kenstevens.stratinit.client.site.translator;

import com.kenstevens.stratinit.client.model.BattleLogEntry;
import com.kenstevens.stratinit.dto.SIBattleLog;
import com.kenstevens.stratinit.type.Constants;
import org.springframework.stereotype.Service;

@Service
public class BattleLogListTranslator extends ListTranslator<SIBattleLog, BattleLogEntry> {

	@Override
	public BattleLogEntry translate(SIBattleLog input) {
		BattleLogEntry battleLog = new BattleLogEntry(input);
		if (battleLog.iAmAttacker()) {
			int defenderId = battleLog.getDefenderId();
			if (defenderId != Constants.UNASSIGNED) {
				battleLog.setOpponent(db.getNation(defenderId).toString());
			}
		}  else {
			battleLog.setOpponent(db.getNation(battleLog.getAttackerId()).toString());
		}
		return battleLog;
	}

}
