package com.kenstevens.stratinit.model;

import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.UnitBaseType;
import com.kenstevens.stratinit.type.UnitType;


public class AirUnitBase extends UnitBase {

	public AirUnitBase(UnitType type, int tech, int productionTime, int movement, int sight,
			int attack, int ammo, int hp) {
		super(type, CityType.AIRPORT, tech, productionTime, movement, sight, attack, ammo, hp);
		setRequiresFuel(true);
	}

	@Override
	protected UnitBaseType getUnitBaseType() {
		return UnitBaseType.AIR;
	}

}
