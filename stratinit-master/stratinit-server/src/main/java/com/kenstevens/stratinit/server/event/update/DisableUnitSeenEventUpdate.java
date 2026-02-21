package com.kenstevens.stratinit.server.event.update;

import com.kenstevens.stratinit.client.model.UnitSeenPK;
import com.kenstevens.stratinit.server.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class DisableUnitSeenEventUpdate extends EventUpdate {
    private final UnitSeenPK unitSeenPK;
    @Autowired
    private UnitService unitService;

    public DisableUnitSeenEventUpdate(UnitSeenPK unitSeenPK) {
        this.unitSeenPK = unitSeenPK;
    }

    @Override
    protected void executeWrite() {
        unitService.disable(unitSeenPK);
    }
}
