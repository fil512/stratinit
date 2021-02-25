package com.kenstevens.stratinit.server.event.svc;

import com.kenstevens.stratinit.model.CityPK;
import com.kenstevens.stratinit.model.RelationPK;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitSeenPK;

import java.util.Date;

public interface StratInitUpdater {

    void updateTech(Integer gameId, Date date);

    void switchRelation(RelationPK relationPK);

    void updateUnit(Unit unit, Date date);

    void buildUnit(CityPK cityPK, Date date);

    void disable(UnitSeenPK unitSeenPK);

    void endGame(Integer gameId);

    void startGame(Integer gameId);

    void mapGame(Integer gameId);
}