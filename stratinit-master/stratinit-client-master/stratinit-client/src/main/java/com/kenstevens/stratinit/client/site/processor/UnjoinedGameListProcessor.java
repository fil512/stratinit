package com.kenstevens.stratinit.client.site.processor;

import com.kenstevens.stratinit.client.event.GameListArrivedEvent;
import com.kenstevens.stratinit.client.site.translator.GameListTranslator;
import com.kenstevens.stratinit.dto.SIGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnjoinedGameListProcessor extends Processor {
	@Autowired
	private GameListTranslator translator;
	
	public void process(List<SIGame> entries) {
		db.getUnjoinedGameList().addAll(translator.translate(entries));
		arrivedDataEventAccumulator.addEvent(new GameListArrivedEvent(false));
	}
}
