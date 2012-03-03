package com.kenstevens.stratinit.site.processor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dto.SILaunchedSatellite;
import com.kenstevens.stratinit.site.translator.SatelliteTranslator;

@Service
public class SatelliteProcessor extends Processor {
	@Autowired
	private SatelliteTranslator translator;
	public void process(List<SILaunchedSatellite> entries) {
		db.getSatelliteList().clear();
		db.getSatelliteList().addAll(translator.translate(entries));
		// TODO BUG interesting that no-one was observing the satellite list...
	}
}
