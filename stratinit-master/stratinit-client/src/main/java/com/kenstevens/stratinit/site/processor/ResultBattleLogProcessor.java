package com.kenstevens.stratinit.site.processor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.audio.WavPlayer;
import com.kenstevens.stratinit.dto.SIBattleLog;
import com.kenstevens.stratinit.model.BattleLogEntry;
import com.kenstevens.stratinit.type.NewsCategory;

@Service
public class ResultBattleLogProcessor {
	@Autowired
	private WavPlayer wavPlayer;

	enum Event {
		CONQUEST, I_DIED, THEY_DIED, I_HIT
	}
	
	public void process(List<SIBattleLog> silogs) {
		Event event = null;
		
		for (SIBattleLog silog : silogs) {
			BattleLogEntry battleLog = new BattleLogEntry(silog);
			if (battleLog.getType() == NewsCategory.CONQUEST) {
				event = Event.CONQUEST;
				break;
			} else if (battleLog.isIDied()) {
				event = Event.I_DIED;
			} else if (battleLog.isOtherDied()) {
				event = Event.THEY_DIED;
			} else if (battleLog.getType() == NewsCategory.NEWS_FROM_THE_FRONT) {
				event = Event.I_HIT;
			}
		}
		if (event != null) {
			if (event == Event.CONQUEST) {
				wavPlayer.playFanfare();
			} else if (event == Event.I_DIED) {
				wavPlayer.playIDied();
			} else if (event == Event.THEY_DIED) {
				wavPlayer.playExplosion();
			} else if (event == Event.I_HIT) {
				wavPlayer.playHit();
			}
		}
	}
}
