package com.kenstevens.stratinit.client.model;

import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.UnitBaseType;
import com.kenstevens.stratinit.type.UnitType;

public class NavalUnitBase extends UnitBase {

	public NavalUnitBase(UnitType type, int tech, int productionTime, int movement,
			int sight, int attack, int ammo, int hp) {
		super(type, CityType.PORT, tech, productionTime, movement, sight, attack, ammo, hp);
	}

	@Override
	public UnitBaseType getUnitBaseType() {
		return UnitBaseType.NAVY;
	}

}
