package com.kenstevens.stratinit.site.processor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.event.GameListArrivedEvent;
import com.kenstevens.stratinit.site.translator.GameListTranslator;

@Service
public class UnjoinedGameListProcessor extends Processor {
	@Autowired
	private GameListTranslator translator;
	
	public void process(List<SIGame> entries) {
		db.getUnjoinedGameList().addAll(translator.translate(entries));
		arrivedDataEventAccumulator.addEvent(new GameListArrivedEvent(false));
	}
}
