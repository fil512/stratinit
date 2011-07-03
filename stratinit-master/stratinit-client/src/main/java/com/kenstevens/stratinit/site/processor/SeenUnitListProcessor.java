package com.kenstevens.stratinit.site.processor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.site.translator.UnitListTranslator;

@Service
public class SeenUnitListProcessor extends Processor {
	@Autowired
	private UnitListTranslator translator;
	public void process(List<SIUnit> entries) {
		db.getSeenUnitList().addAll(translator.translate(entries));
	}
}
