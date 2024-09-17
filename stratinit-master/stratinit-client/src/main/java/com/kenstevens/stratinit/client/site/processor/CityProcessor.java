package com.kenstevens.stratinit.client.site.processor;

import com.kenstevens.stratinit.client.event.CityListArrivedEvent;
import com.kenstevens.stratinit.client.event.WorldArrivedEvent;
import com.kenstevens.stratinit.client.model.WorldSector;
import com.kenstevens.stratinit.client.site.translator.CityListTranslator;
import com.kenstevens.stratinit.dto.SICityUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CityProcessor extends Processor {
    @Autowired
    private CityListTranslator translator;

    public void process(SICityUpdate sicity) {
        db.getCityList().add(translator.translate(sicity));
        arrivedDataEventAccumulator.addEvent(new CityListArrivedEvent());

        WorldSector worldSector = db.getWorld().getWorldSectorOrNull(sicity.coords);
        if (worldSector.getCityType() != sicity.type) {
            worldSector.setCityType(sicity.type);
            arrivedDataEventAccumulator.addEvent(new WorldArrivedEvent());
        }
    }
}
