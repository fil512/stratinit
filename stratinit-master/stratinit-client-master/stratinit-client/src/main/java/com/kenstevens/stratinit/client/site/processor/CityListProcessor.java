package com.kenstevens.stratinit.client.site.processor;

import com.kenstevens.stratinit.client.event.CityListArrivedEvent;
import com.kenstevens.stratinit.client.site.translator.CityListTranslator;
import com.kenstevens.stratinit.dto.SICityUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityListProcessor extends Processor {
    @Autowired
    private CityListTranslator translator;

    public void process(List<SICityUpdate> entries) {
        db.getCityList().addAll(translator.translate(entries));
        arrivedDataEventAccumulator.addEvent(new CityListArrivedEvent());
    }
}
