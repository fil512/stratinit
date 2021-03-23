package com.kenstevens.stratinit.client.site.processor;

import com.kenstevens.stratinit.client.site.translator.SatelliteTranslator;
import com.kenstevens.stratinit.dto.SILaunchedSatellite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
