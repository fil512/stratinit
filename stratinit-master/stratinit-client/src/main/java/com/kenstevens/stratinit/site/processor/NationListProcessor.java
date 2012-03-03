package com.kenstevens.stratinit.site.processor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.event.NationListArrivedEvent;
import com.kenstevens.stratinit.site.translator.NationListTranslator;

@Service
public class NationListProcessor extends Processor {
	@Autowired
	NationListTranslator translator;

	public void process(List<SINation> entries) {
		db.getNationList().addAll(translator.translate(entries));
		arrivedDataEventAccumulator.addEvent(new NationListArrivedEvent());
	}
}
