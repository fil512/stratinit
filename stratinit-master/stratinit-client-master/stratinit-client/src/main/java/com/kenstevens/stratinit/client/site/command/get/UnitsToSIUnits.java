package com.kenstevens.stratinit.client.site.command.get;

import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.dto.SIUnit;

import java.util.List;
import java.util.stream.Collectors;

public final class UnitsToSIUnits {
	private UnitsToSIUnits() {
	}

	public static List<SIUnit> transform(List<UnitView> units) {
		return units.stream().map(SIUnit::new).collect(Collectors.toList());
	}
}
