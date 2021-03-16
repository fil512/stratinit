package com.kenstevens.stratinit.client.site.processor;

import com.kenstevens.stratinit.client.audio.WavPlayer;
import com.kenstevens.stratinit.client.model.BattleLogEntry;
import com.kenstevens.stratinit.dto.SIBattleLog;
import com.kenstevens.stratinit.type.NewsCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultBattleLogProcessor {
	@Autowired
	private WavPlayer wavPlayer;

	enum Event {
		CONQUEST, I_DIED, THEY_DIED, I_HIT
	}

	public void process(List<SIBattleLog> silogs) {
		Event event = getPrimaryEvent(silogs);
		playSound(event);
	}

	private Event getPrimaryEvent(List<SIBattleLog> silogs) {
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
		return event;
	}

	private void playSound(Event event) {
		if (event == null) {
			return;
		}
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
