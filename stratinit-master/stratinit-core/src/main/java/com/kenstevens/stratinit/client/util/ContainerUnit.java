package com.kenstevens.stratinit.client.util;

import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.WorldSector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ContainerUnit {
	private final Unit holder;
	private final Collection<Unit> units;

	public ContainerUnit(Unit holder, Collection<Unit> units) {
		this.holder = holder;
		this.units = units;
	}

	public List<Unit> getPassengers(WorldSector worldSector) {
		return getPassengers(worldSector, null);
	}

	public List<Unit> getPassengers(WorldSector worldSector, Collection<Unit> exclude) {
		List<Unit> passengers = new ArrayList<Unit>();
		if (!holder.carriesUnits()) {
			return passengers;
		}
		int weight = 0;
		for (Unit unit : units) {
			if (exclude != null && exclude.contains(unit)) {
				continue;
			}
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
