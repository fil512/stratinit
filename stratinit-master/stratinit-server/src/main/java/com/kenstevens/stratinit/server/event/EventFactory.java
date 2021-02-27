package com.kenstevens.stratinit.server.event;

import com.kenstevens.stratinit.model.*;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;

@Service
public class EventFactory {

    @Lookup
    public CityBuildEvent getCityBuildEvent(City city) {
        return new CityBuildEvent(city);
    }

    @Lookup
    public UnitSeenEvent getUnitSeenEvent(UnitSeen unitSeen) {
        return new UnitSeenEvent(unitSeen);
    }

    @Lookup
    public GameStartEvent getGameStartEvent(Game game) {
        return new GameStartEvent(game);
    }

    @Lookup
    public GameMapEvent getGameMapEvent(Game game) {
        return new GameMapEvent(game);
    }

    @Lookup
    public GameEndEvent getGameEndEvent(Game game) {
        return new GameEndEvent(game);
    }

    @Lookup
    public TechUpdateEvent getTechUpdateEvent(Game game) {
        return new TechUpdateEvent(game);
    }

    @Lookup
    public UnitUpdateEvent getUnitUpdateEvent(Unit unit) {
        return new UnitUpdateEvent(unit);
    }

    @Lookup
    public RelationChangeEvent getRelationChangeEvent(Relation relation) {
        return new RelationChangeEvent(relation);
    }
}