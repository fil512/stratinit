package com.kenstevens.stratinit.wicket.provider;

import com.kenstevens.stratinit.type.UnitType;

public class UnitDay {

	public final int day;
	public final UnitType unitType;

	public UnitDay(int day, UnitType unitType) {
		this.day = day;
		this.unitType = unitType;
	}

	public int getDay() {
		return day;
	}

	public UnitType getUnitType() {
		return unitType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + day;
		result = prime * result
				+ ((unitType == null) ? 0 : unitType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UnitDay other = (UnitDay) obj;
		if (day != other.day)
			return false;
		if (unitType != other.unitType)
			return false;
		return true;
	}

}