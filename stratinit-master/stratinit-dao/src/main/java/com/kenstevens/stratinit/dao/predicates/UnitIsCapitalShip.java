package com.kenstevens.stratinit.dao.predicates;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.model.Unit;

public class UnitIsCapitalShip implements Predicate<Unit> {
	public boolean apply(Unit unit) {
		return unit.isCapital();
	}

}
