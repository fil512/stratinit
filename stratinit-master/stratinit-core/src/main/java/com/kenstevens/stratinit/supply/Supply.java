package com.kenstevens.stratinit.supply;

import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.move.WorldView;

public class Supply {
	private final WorldView worldView;

	public Supply(WorldView worldView) {
		this.worldView = worldView;
	}

	public boolean inSupply(Unit unit) {
		if (!unit.requiresSupply()) {
			return true;
		}
		if (unit.requiresFuel()) {
			return worldView.fueling(unit);
		}

		return !worldView.getSupplyingSectors(unit).isEmpty();
	}

	public void resupply(Unit unit) {
		if (unit.requiresFuel()) {
			unit.resupply();
		} else if (unit.atMaxAmmo()) {
			return;
		} else {
			SupplyTree supplyTree = new SupplyTree(worldView, unit);
			supplyTree.resupply();
		}
	}

	public WorldView getWorldView() {
		return worldView;
	}
}
