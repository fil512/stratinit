package com.kenstevens.stratinit.site.processor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.event.UnitListArrivedEvent;
import com.kenstevens.stratinit.site.translator.UnitListTranslator;

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
