package com.kenstevens.stratinit.client.site.processor;

import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.model.stats.StatsHolder;
import com.kenstevens.stratinit.client.rest.StratInitServerClient;
import com.kenstevens.stratinit.dto.SIBattleLog;
import com.kenstevens.stratinit.dto.SIUnitBuilt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class StatsManager {
	@Autowired
	private Data db;
	@Autowired
	private StratInitServerClient stratInitServer;
	@Autowired
	private BattleLogProcessor battleLogProcessor;
	
	private final StatsHolder statsHolder = new StatsHolder();
	
	public void update(int gameId) {
		downloadBattleLogs();
		statsHolder.clear();
		// TODO REF Move to ActionFactory with Observer
		List<SIUnitBuilt> unitsBuilt = stratInitServer.getUnitsBuilt().getValue();
		statsHolder.addBuilt(unitsBuilt);
		statsHolder.rebuildStats(db.getBattleLogList());
	}

	// TODO REF this is in the wrong place
	private void downloadBattleLogs() {
		List<SIBattleLog> silogs = stratInitServer.getBattleLog().getValue();
		battleLogProcessor.process(silogs);
	}

	public Set<String> getOpponents() {
		return statsHolder.getOpponents();
	}

	public List<String[]> getOpponentStats() {
		return statsHolder.getOpponentStats();
	}

	public List<String[]>  getOpponentStats(String opponent) {
		return statsHolder.getOpponentStats(opponent);
	}

	public List<String[]>  getUnitRecordStats() {
		return statsHolder.getUnitRecordStats();
	}

	public String getWorstOpponent() {
		return statsHolder.getWorstOpponent();
	}
}
