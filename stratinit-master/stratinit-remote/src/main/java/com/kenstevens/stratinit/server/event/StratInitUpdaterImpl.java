package com.kenstevens.stratinit.server.event;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.main.Spring;
import com.kenstevens.stratinit.model.CityPK;
import com.kenstevens.stratinit.model.RelationPK;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitSeenPK;

@Service
public class StratInitUpdaterImpl implements StratInitUpdater {
	@Autowired
	private Spring spring;

	@Override
	public void updateTech(Integer gameId, Date date) {
		spring.autowire(new TechEventUpdate(date)).update(gameId);
	}

	@Override
	public void buildUnit(CityPK cityPK, Date date) {
		spring.autowire(new BuildUnitEventUpdate(cityPK, date)).update(cityPK.getGameId());
	}

	@Override
	public void endGame(Integer gameId) {
		spring.getBean(EndGameEventUpdate.class).update(gameId);
	}

	@Override
	public void startGame(Integer gameId) {
		spring.getBean(StartGameEventUpdate.class).update(gameId);
	}

	@Override
	public void switchRelation(RelationPK relationPK) {
		spring.autowire(new SwitchRelationEventUpdate(relationPK)).update(relationPK.getGameId());
	}

	@Override
	public void disable(UnitSeenPK unitSeenPK) {
		spring.autowire(new DisableUnitSeenEventUpdate(unitSeenPK)).update(unitSeenPK.getGameId());
	}

	@Override
	public void updateUnit(Unit unit, Date date) {
		spring.autowire(new UpdateUnitEventUpdate(unit, date)).update(unit.getGameId());
	}

	@Override
	public void mapGame(Integer gameId) {
		spring.getBean(MapGameEventUpdate.class).update(gameId);

	}
}
