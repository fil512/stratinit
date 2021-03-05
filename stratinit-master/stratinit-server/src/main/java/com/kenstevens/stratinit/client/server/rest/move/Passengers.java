package com.kenstevens.stratinit.client.server.rest.move;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.WorldSector;
import com.kenstevens.stratinit.client.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Scope("prototype")
@Component
public class Passengers implements Iterable<Unit> {
	private final UnitsToMove unitsToMove;
	private final Multimap<Unit, Unit> passengerMap = HashMultimap.create();
	private final WorldView worldView;
	@Autowired
	UnitDaoService unitDaoService;

	public Passengers(UnitsToMove unitsToMove, WorldView worldView) {
		this.unitsToMove = unitsToMove;
		this.worldView = worldView;
	}

	public static Passengers copyFrom(Passengers passengers, UnitsToMove newUnitsToMove) {
		Passengers retval = new Passengers(newUnitsToMove, passengers.worldView);
		for (Unit unit : newUnitsToMove) {
			Collection<Unit> children = passengers.passengerMap.get(unit);
			if (children != null && !children.isEmpty()) {
				retval.passengerMap.putAll(unit, children);
			}
		}
		return retval;
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void addPassengers() {
		SectorCoords startCoords = unitsToMove.getFirstCoords();
		WorldSector worldSector;
		if (startCoords == null) {
			worldSector = null;
		} else {
			worldSector = worldView.getWorldSector(startCoords);
		}
		if (worldSector == null) {
			return;
		}
		for (Unit unit : unitsToMove) {
			if (unit.carriesUnits()) {
				addPassengers(unit, worldSector);
			}
		}
	}

	private void addPassengers(Unit holder, WorldSector worldSector) {
		List<Unit> passengers = unitDaoService.getPassengers(holder,
				worldSector, passengerMap.values());
		passengerMap.putAll(holder, passengers);
	}

	public Collection<Unit> getPassengers(Unit holder) {
		return passengerMap.get(holder);
	}

	@Override
	public Iterator<Unit> iterator() {
		return passengerMap.values().iterator();
	}
}
