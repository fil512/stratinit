package com.kenstevens.stratinit.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kenstevens.stratinit.type.SectorCoords;

@Repository
public class SelectedUnits implements Iterable<UnitView> {
	private final List<UnitView> units = new ArrayList<UnitView>();

	public void setUnits(Iterable<UnitView> units) {
		this.units.clear();
		for (UnitView unit : units) {
			this.units.add(unit);
		}
	}

	@Override
	public Iterator<UnitView> iterator() {
		return units.iterator();
	}

	public SectorCoords getCoords() {
		if (units.size() > 0) {
			return units.get(0).getCoords();
		}
		return null;
	}
	
	public boolean isEmpty() {
		return units.isEmpty();
	}

	public void copyTo(Collection<UnitView> list) {
		for (UnitView unit : units) {
			list.add(unit);
		}
	}

	public void clear() {
		units.clear();
	}

	public boolean contains(UnitView unit) {
		return units.contains(unit);
	}

	public boolean hasMoves() {
		for (UnitView unit : units) {
			if (unit.getMobility() > 0) {
				return true;
			}
		}
		return false;
	}
}
