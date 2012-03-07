package com.kenstevens.stratinit.site.command;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitView;

public final class UnitsToSIUnits {
	private UnitsToSIUnits() {}
	
	public static List<SIUnit> transform(List<UnitView> units) {
		return Lists.newArrayList(Lists.transform(units, new Function<Unit, SIUnit>() {
			@Override
			public SIUnit apply(Unit unit) {
				return new SIUnit(unit);
			}
		}));
	}


}
