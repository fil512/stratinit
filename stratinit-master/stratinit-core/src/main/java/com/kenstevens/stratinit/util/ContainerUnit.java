package com.kenstevens.stratinit.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.WorldSector;


public class ContainerUnit {
	private final Unit holder;
	private final Collection<Unit> units;

	public ContainerUnit(Unit holder, Collection<Unit> units) {
		this.holder = holder;
		this.units = units;
	}

	public List<Unit> getPassengers(WorldSector worldSector) {
		List<Unit> passengers = new ArrayList<Unit>();
		if (!holder.carriesUnits()) {
			return passengers;
		}
		int weight = 0;
		for (Unit unit : units) {
			if (UnitHelper.canCarry(holder, unit, worldSector)) {
				weight += unit.getWeight();
				if (weight > holder.getCapacity()) {
					break;
				}
				passengers.add(unit);
			}
		}
		return passengers;
	}

}
