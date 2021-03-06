package com.kenstevens.stratinit.wicket.provider;

import com.kenstevens.stratinit.client.model.UnitBase;
import com.kenstevens.stratinit.type.UnitType;

public class PlayerUnitCount extends UnitDay {
	private final int count;

	public PlayerUnitCount(int day, UnitType unitType, int count) {
		super(day, unitType);
		this.count = count;
	}

	public int getCount() {
		return count;
	}
	
	public int getCost() {
		return UnitBase.getUnitBase(unitType).getProductionTime() * count;
	}
}
