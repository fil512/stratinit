package com.kenstevens.stratinit.server.rest.move;

import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.server.service.UnitService;
import com.kenstevens.stratinit.type.SectorCoords;

import java.util.*;

public class Interceptors {
	Map<Unit, SectorCoords> startCoords = new HashMap<>();
	Map<SectorCoords, List<Unit>> groups = new HashMap<>();
	List<Unit> units = new ArrayList<Unit>();

	public void add(Unit unit) {
		units.add(unit);
		SectorCoords coords = unit.getCoords();
		startCoords.put(unit, coords);
		List<Unit> group = groups.get(coords);
		if (group == null) {
			group = new ArrayList<>();
			groups.put(coords, group);
		}
		group.add(unit);
	}

	public int size() {
		return units.size();
	}

	public boolean isEmpty() {
		return units.isEmpty();
	}

	public void flyBack(UnitService unitService, WorldView worldView) {
		for (Unit unit : units) {
			flyBack(unitService, worldView, startCoords, unit);
		}
	}

	public void flyBack(UnitService unitService, WorldView worldView,
						Map<Unit, SectorCoords> startCoords, Unit unit) {
		if (unit.isAlive()) {
			// Fly back for free
			unit.setCoords(startCoords.get(unit));
			// Resupply
			if (worldView.fueling(unit)) {
				unit.resupply();
			}
			unitService.merge(unit);
		}
	}

	public Collection<List<Unit>> unitsBySector() {
		return groups.values();
	}

}
