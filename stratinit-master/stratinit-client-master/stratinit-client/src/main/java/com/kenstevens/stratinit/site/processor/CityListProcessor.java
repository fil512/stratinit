package com.kenstevens.stratinit.site.processor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.event.CityListArrivedEvent;
import com.kenstevens.stratinit.site.translator.CityListTranslator;

@Service
public class CityListProcessor extends Processor {
	@Autowired
	private CityListTranslator translator;
	
	public void process(List<SICity> entries) {
		db.getCityList().addAll(translator.translate(entries));
		arrivedDataEventAccumulator.addEvent(new CityListArrivedEvent());
	}
}
