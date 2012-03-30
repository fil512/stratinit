package com.kenstevens.stratinit.wicket.unit;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.model.GameHistory;

public class GameUnitsBuilt {
	private final List<UnitsBuilt> unitsBuilt = Lists.newArrayList();
	private final GameHistory game;

	public GameUnitsBuilt(GameHistory game) {
		this.game = game;
	}

	public List<UnitsBuilt> getUnitsBuilt() {
		return unitsBuilt;
	}

	public void addAll(Collection<? extends UnitsBuilt> list) {
		unitsBuilt.addAll(list);
	}

	public GameHistory getGame() {
		return game;
	}
}
