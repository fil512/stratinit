package com.kenstevens.stratinit.supply;

import com.google.common.base.Predicate;

public class HasAmmoPredicate implements Predicate<SupplyNode> {
	@Override
	public boolean apply(SupplyNode supplyNode) {
		return ((UnitSupplyNode)supplyNode).hasAmmo();
	}

}
