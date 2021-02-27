package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.remote.request.UnitListJson;

import java.util.List;
import java.util.stream.Collectors;

public final class UnitsToSIUnits {
	private UnitsToSIUnits() {
	}

	public static List<SIUnit> transform(List<UnitView> units) {
		return units.stream().map(SIUnit::new).collect(Collectors.toList());
	}

	public static List<SIUnit> transform(UnitListJson unitListJson) {
		return unitListJson.units.stream().map(SIUnit::new).collect(Collectors.toList());
	}
}
