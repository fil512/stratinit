package com.kenstevens.stratinit.ui.tabs;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.type.UnitType;

public class TreeUnitsByType {

	private final Multimap<UnitType, UnitView> unitsByType;

	public TreeUnitsByType(Data db) {
		unitsByType = Multimaps.index(db.getUnitList(), new Function<Unit, UnitType>() {

			@Override
			public UnitType apply(Unit unit) {
				return unit.getType();
			}
		});
	}

	public Set<UnitType> keySet() {
		return unitsByType.keySet();
	}

	public int count(UnitType type) {
		return unitsByType.get(type).size();
	}

	public Collection<UnitView> get(UnitType type) {
		return unitsByType.get(type);
	}

}
