package com.kenstevens.stratinit.site.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.event.CityListArrivedEvent;
import com.kenstevens.stratinit.event.WorldArrivedEvent;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.site.translator.CityListTranslator;

@Service
public class CityProcessor extends Processor {
	@Autowired
	private CityListTranslator translator;
	
	public void process(SICity sicity) {
		db.getCityList().add(translator.translate(sicity));
		arrivedDataEventAccumulator.addEvent(new CityListArrivedEvent());
		
		WorldSector worldSector = db.getWorld().getWorldSector(sicity.coords);
		if (worldSector.getCityType() != sicity.type) {
			worldSector.setCityType(sicity.type);
			arrivedDataEventAccumulator.addEvent(new WorldArrivedEvent());
		}
	}
}
