package com.kenstevens.stratinit.client.site.processor;

import com.kenstevens.stratinit.client.site.translator.SectorListTranslator;
import com.kenstevens.stratinit.dto.SISector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectorListProcessor extends Processor {
	@Autowired
	private SectorListTranslator translator;

	public void process(List<SISector> entries) {
		db.getWorld().addAll(
				translator.translate(entries));
	}
}
