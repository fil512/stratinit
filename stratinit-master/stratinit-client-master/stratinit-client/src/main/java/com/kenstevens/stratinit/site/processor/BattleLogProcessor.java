package com.kenstevens.stratinit.site.processor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dto.SIBattleLog;
import com.kenstevens.stratinit.event.BattleLogListArrivedEvent;
import com.kenstevens.stratinit.event.NationListArrivedEvent;
import com.kenstevens.stratinit.site.translator.BattleLogListTranslator;

@Service
public class BattleLogProcessor extends Processor {
	@Autowired
	private BattleLogListTranslator battleLogListTranslator;
	
	public void process(List<SIBattleLog> entries) {
		db.getBattleLogList().addAll(battleLogListTranslator.translate(entries));
		arrivedDataEventAccumulator.addEvent(new BattleLogListArrivedEvent());
		db.getNation().setNewBattle(false);
		arrivedDataEventAccumulator.addEvent(new NationListArrivedEvent());
	}
}
