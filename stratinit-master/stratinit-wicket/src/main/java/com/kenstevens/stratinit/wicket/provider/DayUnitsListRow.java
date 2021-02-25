package com.kenstevens.stratinit.wicket.provider;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.type.UnitType;

import java.util.List;

public class DayUnitsListRow {
	private final List<String> values = Lists.newArrayList();
	private final UnitType unitType;

	public DayUnitsListRow(UnitType unitType, List<Integer> unitCountList) {
		this.unitType = unitType;
		for (int unitCount : unitCountList) {
			values.add("" + unitCount);
		}
	}
	
	public List<String> getValues() {
		return values;
	}

	public String getUnitType() {
		return unitType.toString();
	}
}
