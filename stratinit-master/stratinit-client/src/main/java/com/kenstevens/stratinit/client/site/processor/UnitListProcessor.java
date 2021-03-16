package com.kenstevens.stratinit.client.site.processor;

import com.kenstevens.stratinit.client.event.UnitListArrivedEvent;
import com.kenstevens.stratinit.client.site.translator.UnitListTranslator;
import com.kenstevens.stratinit.dto.SIUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitListProcessor extends Processor {
	@Autowired
	private UnitListTranslator translator;
	public void process(List<SIUnit> entries, boolean firstTime) {
		db.getUnitList().addAll(translator.translate(entries));
		arrivedDataEventAccumulator.addEvent(new UnitListArrivedEvent());
		// TODO BUG this is broken
		db.getUnitList().setFlags(db.getLastLoginTime(), firstTime);
	}
}
