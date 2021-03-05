package com.kenstevens.stratinit.client.server.event.update;

import com.kenstevens.stratinit.client.model.UnitSeenPK;
import com.kenstevens.stratinit.client.server.daoservice.UnitDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class DisableUnitSeenEventUpdate extends EventUpdate {
    private final UnitSeenPK unitSeenPK;
    @Autowired
    private UnitDaoService unitDaoService;

    public DisableUnitSeenEventUpdate(UnitSeenPK unitSeenPK) {
        this.unitSeenPK = unitSeenPK;
    }

    @Override
    protected void executeWrite() {
        unitDaoService.disable(unitSeenPK);
    }
}
