package com.kenstevens.stratinit.model;

import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.UnitBaseType;
import com.kenstevens.stratinit.type.UnitType;

public class BaseUnitBase extends UnitBase {
	public BaseUnitBase(UnitType type, int tech, int productionTime, int mobility, int sight,
			int attack, int ammo, int hp) {
		super(type, CityType.BASE, tech, productionTime, mobility, sight, attack, ammo, hp);
	}

	@Override
	protected UnitBaseType getUnitBaseType() {
		return null;
	}
}
