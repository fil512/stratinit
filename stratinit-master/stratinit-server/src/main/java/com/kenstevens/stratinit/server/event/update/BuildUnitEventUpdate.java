package com.kenstevens.stratinit.server.event.update;

import com.kenstevens.stratinit.client.model.CityPK;
import com.kenstevens.stratinit.server.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Scope("prototype")
@Component
public class BuildUnitEventUpdate extends EventUpdate {
    private final Date date;
    private final CityPK cityPK;
    @Autowired
    private CityService cityService;

    public BuildUnitEventUpdate(CityPK cityPK, Date date) {
        this.cityPK = cityPK;
        this.date = date;
    }

    @Override
    protected void executeWrite() {
        cityService.buildUnit(cityPK, date);
    }
}
