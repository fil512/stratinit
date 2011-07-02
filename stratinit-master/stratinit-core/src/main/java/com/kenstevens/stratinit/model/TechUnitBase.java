package com.kenstevens.stratinit.model;

import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.UnitBaseType;
import com.kenstevens.stratinit.type.UnitType;

public class TechUnitBase extends UnitBase {

	public TechUnitBase(UnitType type, int tech, int productionTime, int movement,
			int sight, int attack, int ammo, int hp) {
		super(type, CityType.TECH, tech, productionTime, movement, sight, attack, ammo, hp);
	}

	@Override
	protected UnitBaseType getUnitBaseType() {
		return UnitBaseType.TECH;
	}

}
