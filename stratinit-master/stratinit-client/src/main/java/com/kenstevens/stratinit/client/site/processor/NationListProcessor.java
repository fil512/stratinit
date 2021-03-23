package com.kenstevens.stratinit.client.site.processor;

import com.kenstevens.stratinit.client.event.NationListArrivedEvent;
import com.kenstevens.stratinit.client.site.translator.NationListTranslator;
import com.kenstevens.stratinit.dto.SINation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NationListProcessor extends Processor {
	@Autowired
	NationListTranslator translator;

	public void process(List<SINation> entries) {
		db.getNationList().addAll(translator.translate(entries));
		arrivedDataEventAccumulator.addEvent(new NationListArrivedEvent());
	}
}
