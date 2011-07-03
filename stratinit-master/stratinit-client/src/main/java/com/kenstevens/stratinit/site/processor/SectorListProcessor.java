package com.kenstevens.stratinit.site.processor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dto.SISector;
import com.kenstevens.stratinit.site.translator.SectorListTranslator;

@Service
public class SectorListProcessor extends Processor {
	@Autowired
	private SectorListTranslator translator;

	public void process(List<SISector> entries) {
		db.getWorld().addAll(
				translator.translate(entries));
	}
}
