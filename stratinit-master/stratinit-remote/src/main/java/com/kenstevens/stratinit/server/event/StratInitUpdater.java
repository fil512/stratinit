package com.kenstevens.stratinit.server.event;

import java.util.Date;

import com.kenstevens.stratinit.model.CityPK;
import com.kenstevens.stratinit.model.RelationPK;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitSeenPK;

public interface StratInitUpdater {

	public abstract void updateTech(Integer gameId, Date date);

	public abstract void switchRelation(RelationPK relationPK);

	public abstract void updateUnit(Unit unit, Date date);

	public abstract void buildUnit(CityPK cityPK, Date date);

	public abstract void disable(UnitSeenPK unitSeenPK);

	public abstract void endGame(Integer gameId);

	public abstract void startGame(Integer gameId);

	public abstract void mapGame(Integer gameId);
}