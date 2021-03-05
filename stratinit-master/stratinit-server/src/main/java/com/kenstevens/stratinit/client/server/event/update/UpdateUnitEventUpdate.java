package com.kenstevens.stratinit.client.server.event.update;

import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.server.daoservice.UnitDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Scope("prototype")
@Component
public class UpdateUnitEventUpdate extends EventUpdate {
    private final Unit unit;
    private final Date date;
    @Autowired
    private UnitDaoService unitDaoService;

    public UpdateUnitEventUpdate(Unit unit, Date date) {
        this.unit = unit;
        this.date = date;
    }

    @Override
    protected void executeWrite() {
        unitDaoService.updateUnit(unit, date);
    }
}
