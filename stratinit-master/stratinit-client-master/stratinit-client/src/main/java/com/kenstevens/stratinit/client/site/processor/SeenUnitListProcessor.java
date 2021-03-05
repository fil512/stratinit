package com.kenstevens.stratinit.client.site.processor;

import com.kenstevens.stratinit.client.site.translator.UnitListTranslator;
import com.kenstevens.stratinit.dto.SIUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeenUnitListProcessor extends Processor {
	@Autowired
	private UnitListTranslator translator;
	public void process(List<SIUnit> entries) {
		db.getSeenUnitList().addAll(translator.translate(entries));
	}
}
