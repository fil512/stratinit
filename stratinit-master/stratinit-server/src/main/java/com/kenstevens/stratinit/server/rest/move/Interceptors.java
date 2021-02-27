package com.kenstevens.stratinit.server.rest.move;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.type.SectorCoords;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Interceptors {
	Map<Unit, SectorCoords> startCoords = Maps.newHashMap();
	Map<SectorCoords, List<Unit>> groups = Maps.newHashMap();
	List<Unit> units = new ArrayList<Unit>();

	public void add(Unit unit) {
		units.add(unit);
		SectorCoords coords = unit.getCoords();
		startCoords.put(unit, coords);
		List<Unit> group = groups.get(coords);
		if (group == null) {
			group = Lists.newArrayList();
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

	public void flyBack(UnitDaoService unitDaoService, WorldView worldView) {
		for (Unit unit : units) {
			flyBack(unitDaoService, worldView, startCoords, unit);
		}
	}

	public void flyBack(UnitDaoService unitDaoService, WorldView worldView,
						Map<Unit, SectorCoords> startCoords, Unit unit) {
		if (unit.isAlive()) {
			// Fly back for free
			unit.setCoords(startCoords.get(unit));
			// Resupply
			if (worldView.fueling(unit)) {
				unit.resupply();
			}
			unitDaoService.merge(unit);
		}
	}

	public Collection<List<Unit>> unitsBySector() {
		return groups.values();
	}

}
