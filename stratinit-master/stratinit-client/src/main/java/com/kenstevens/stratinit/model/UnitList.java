package com.kenstevens.stratinit.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

public class UnitList implements Iterable<UnitView> {
	private Map<Integer, UnitView> unitMap = new Hashtable<Integer, UnitView>();

	private void add(UnitView unit) {
		UnitView newUnit = unit;
		if (unitMap.get(unit.getId()) != null) {
			UnitView orig = unitMap.get(unit.getId());
			newUnit = orig.copyFrom(unit);
			newUnit.setDeleted(false);
		}
		unitMap.put(newUnit.getId(), newUnit);
	}

	public void addAll(List<UnitView> units) {
		markDeleted();
		for (UnitView unit : units) {
			add(unit);
		}
		deleteMarked();
	}

	private void remove(Unit unit) {
		unit.setAlive(false);
		unitMap.remove(unit.getId());
	}

	public UnitView get(int id) {
		return unitMap.get(id);
	}

	public int size() {
		return unitMap.size();
	}

	public boolean isEmpty() {
		return unitMap.isEmpty();
	}

	public List<UnitView> unitsAt(SectorCoords coords) {
		List<UnitView>retval = new ArrayList<UnitView>();
		for (UnitView unit : this) {
			if (unit.getCoords().equals(coords)) {
				retval.add(unit);
			}
		}
		return retval;
	}

	@Override
	public Iterator<UnitView> iterator() {
		return unitMap.values().iterator();
	}

	public void markDeleted() {
		for (UnitView unit : this) {
			unit.setDeleted(true);
		}
	}

	private void deleteMarked() {
		List<Unit> unitsToDelete = new ArrayList<Unit>();
		for (UnitView unit : this) {
			if (unit.isDeleted()) {
				unitsToDelete.add(unit);
			}
		}
		for (Unit unit : unitsToDelete) {
			remove(unit);
		}
	}

	public boolean contains(Unit unit) {
		return unitMap.containsKey(unit.getId());
	}

	public List<UnitView> getUnits() {
		// Safe copy to avoid concurrent modification exceptions
		return Lists.newArrayList(unitMap.values());
	}

	public List<UnitView> typedUnitsAt(UnitType type, SectorCoords coords) {
		List<UnitView>retval = new ArrayList<UnitView>();
		List<UnitView>units = unitsAt(coords);
		for (UnitView unit : units) {
			if (unit.getType() == type) {
				retval.add(unit);
			}
		}
		return retval;
	}

	public int getUnitTypeCount(UnitType unitType) {
		int count = 0;
		for (UnitView unit : this) {
			if (unit.getType() == unitType) {
				++count;
			}
		}
		return count;
	}

	public void setFlags(Date lastLoginTime, boolean firstTime) {
		for (UnitView unit : this) {
			unit.setNew(unit.getCreated().after(lastLoginTime));
			if (firstTime) {
				unit.setMoveIncreased(unit.getLastUpdated().after(lastLoginTime));
			}
		}
	}
}
