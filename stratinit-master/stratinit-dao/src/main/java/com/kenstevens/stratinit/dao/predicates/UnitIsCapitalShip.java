package com.kenstevens.stratinit.dao.predicates;

import java.util.function.Predicate;
import com.kenstevens.stratinit.client.model.Unit;

public class UnitIsCapitalShip implements Predicate<Unit> {
	public boolean test(Unit unit) {
		return unit.isCapital();
	}

}
