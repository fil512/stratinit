package com.kenstevens.stratinit.client.site.processor;

import com.kenstevens.stratinit.client.event.BattleLogListArrivedEvent;
import com.kenstevens.stratinit.client.event.NationListArrivedEvent;
import com.kenstevens.stratinit.client.site.translator.BattleLogListTranslator;
import com.kenstevens.stratinit.dto.SIBattleLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
