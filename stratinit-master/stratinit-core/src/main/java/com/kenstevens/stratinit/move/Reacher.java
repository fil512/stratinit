package com.kenstevens.stratinit.move;

import java.util.HashSet;
import java.util.Set;

import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.WorldSector;

public class Reacher {

	private final Unit unit;
	private final WorldView worldView;
	private final WorldSector worldSector;
	private final int movement;
	private int fuelRemaining;
	private Set<WorldSector> reachableSectors;
	private Set<WorldSector> lastAddedSectors;
	private Set<WorldSector> airports;
	private int distance;

	public Reacher(Unit unit, WorldView worldView) {
		this.unit = unit;
		this.worldView = worldView;
		this.worldSector = worldView.getWorldSector(unit);
		this.movement = unit.getMobility();
		if (unit.requiresFuel()) {
			fuelRemaining = unit.getFuel();
		} else {
			fuelRemaining = movement + 1; // Should this be 999?
		}

		reachableSectors = new HashSet<WorldSector>();
		reachableSectors.add(worldSector);
		lastAddedSectors = reachableSectors;
		airports = new HashSet<WorldSector>();
		distance = 1;
	}

	public Set<WorldSector> canReach() {
		// Follow the plane path recording airports and distances.
		Set<WorldSector> pending = new HashSet<WorldSector>();
		while (distance <= fuelRemaining) {
			Set<WorldSector> sectorsToAdd = new HashSet<WorldSector>();
			addPendingSectors(sectorsToAdd, pending);
			for (WorldSector sector : lastAddedSectors) {
				addNextLayerOut(pending, sectorsToAdd, sector);
			}
			reachableSectors.addAll(sectorsToAdd);
			lastAddedSectors = sectorsToAdd;
			distance++;
		}

		// Follow the path out from airports if distance + distanceForAirport <=
		// Fuel we can get there.
		if (unit.requiresFuel()) {
			if (worldSector.canRefuel(unit))
				airports.add(worldSector);
			flyToAirports();
		}
		// Lower the shade for the stuff we don't have the movement points for.
		// This used to just remove stuff outta range.
		for (WorldSector sector : reachableSectors) {
			if (sector.getDistance() > movement) {
				sector.setEnoughMoves(false);
			}
		}
		reachableSectors.remove(worldSector);
		return reachableSectors;
	}

	private void flyToAirports() {
		for (WorldSector nextAirport : airports) {
			distance = 0;
			Set<WorldSector> mappedSectors = new HashSet<WorldSector>();
			mappedSectors.add(nextAirport);
			lastAddedSectors = mappedSectors;
			while (distance < fuelRemaining) {
				distance++;
				Set<WorldSector> sectorsToAdd = new HashSet<WorldSector>();
				for (WorldSector sector : lastAddedSectors) {
					flyOneSectorOut(mappedSectors, sectorsToAdd, sector);
				}
				reachableSectors.addAll(sectorsToAdd);
				lastAddedSectors = sectorsToAdd;
			}
		}
	}

	private void flyOneSectorOut(Set<WorldSector> mappedSectors,
			Set<WorldSector> sectorsToAdd, WorldSector sector) {
		for (WorldSector nextSector : worldView
				.getNeighbours(sector)) {
			if (!reachableSectors.contains(nextSector)) {
				continue;
			}
			if (mappedSectors.contains(nextSector)) {
				continue;
			}

			if (nextSector.getDistance() + distance > fuelRemaining) {
				// The +1 is the last gasp fuel that you can
				// "glide into an airport" or apparently attack
				// an enemy city.
				continue;
			}
			nextSector.setCanReach(true);
			nextSector.setEnoughMoves(true);
			sectorsToAdd.add(nextSector);
		}
	}

	private void addNextLayerOut(Set<WorldSector> pending,
			Set<WorldSector> sectorsToAdd, WorldSector sector) {
		for (WorldSector nextSector : worldView.getNeighbours(sector)) {
			if (reachableSectors.contains(nextSector)) {
				continue;
			}
			if (!nextSector.canEnter(unit)) {
				continue;
			}
			// Note that move cost is based on supply of sector you are LEAVING
			if (!sector.isInSupply() && !unit.requiresFuel() &&!unit.isSupply()) {
				pending.add(nextSector);
				continue;
			}
			processNextSector(sectorsToAdd, nextSector);
		}
	}

	private void addPendingSectors(Set<WorldSector> sectorsToAdd,
			Set<WorldSector> pending) {
		for (WorldSector nextSector : pending) {
			processNextSector(sectorsToAdd, nextSector);
		}
		pending.clear();
	}

	private void processNextSector(Set<WorldSector> sectorsToAdd,
			WorldSector nextSector) {
		if (unit.requiresFuel() && nextSector.canRefuel(unit)) {
			airports.add(nextSector);
		}
		// We need to add airports one outside our normal flying
		// range.
		if (distance < fuelRemaining) {
			nextSector.setDistance(distance);
			nextSector.setCanReach(!unit.requiresFuel());
			nextSector.setEnoughMoves(true);
			sectorsToAdd.add(nextSector);
		}
	}
}
