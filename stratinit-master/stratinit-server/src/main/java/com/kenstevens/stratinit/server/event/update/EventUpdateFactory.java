package com.kenstevens.stratinit.server.event.update;

import com.kenstevens.stratinit.model.CityPK;
import com.kenstevens.stratinit.model.RelationPK;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitSeenPK;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class EventUpdateFactory {
    @Lookup
    public TechEventUpdate getTechEventUpdate(Date date) {
        return new TechEventUpdate(date);
    }

    @Lookup
    public BuildUnitEventUpdate getBuildUnitEventUpdate(CityPK cityPK, Date date) {
        return new BuildUnitEventUpdate(cityPK, date);
    }

    @Lookup
    public SwitchRelationEventUpdate getSwitchRelationEventUpdate(RelationPK relationPK) {
        return new SwitchRelationEventUpdate(relationPK);
    }

    @Lookup
    public DisableUnitSeenEventUpdate getDisableUnitSeenEventUpdate(UnitSeenPK unitSeenPK) {
        return new DisableUnitSeenEventUpdate(unitSeenPK);
    }

    @Lookup
    public UpdateUnitEventUpdate getUpdateUnitEventUpdate(Unit unit, Date date) {
        return new UpdateUnitEventUpdate(unit, date);
    }

    @Lookup
    public EndGameEventUpdate getEndGameEventUpdate() {
        return new EndGameEventUpdate();
    }

    @Lookup
    public StartGameEventUpdate getStartGameEventUpdate() {
        return new StartGameEventUpdate();
    }

    @Lookup
    public MapGameEventUpdate getMapGameEventUpdate() {
        return new MapGameEventUpdate();
    }
}
