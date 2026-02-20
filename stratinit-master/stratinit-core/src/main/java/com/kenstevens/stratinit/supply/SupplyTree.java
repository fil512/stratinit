package com.kenstevens.stratinit.supply;

import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.WorldSector;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.type.SectorCoordVector;
import com.kenstevens.stratinit.world.predicate.UnitSuppliesUnitPredicate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SupplyTree {

	private final UnitSupplyNode rootNode;
	private final Set<Unit> allValues = new HashSet<Unit>();
	private final boolean linkedToPort;

	public SupplyTree(WorldView worldView, Unit rootUnit) {
		this.rootNode = new UnitSupplyNode(null, rootUnit);
		allValues.add(rootUnit);
		linkedToPort = huntForCitySupply(worldView, rootUnit);
	}

	private boolean huntForCitySupply(WorldView worldView, Unit rootUnit) {
		WorldSector worldSector = worldView.getWorldSector(rootUnit);
		if (worldSector.supplies(rootUnit)) {
			addChild(rootNode, worldSector);
			return true;
		}
		return huntForCitySupply(worldView, rootNode);
	}

	public void resupply() {
		if (isLinkedToPort()) {
			rootNode.resupply();
		} else {
			rootNode.pullSupply();
		}
	}

	private final boolean huntForCitySupply(WorldView worldView, UnitSupplyNode supplyNode) {
		Unit targetUnit = supplyNode.getUnit();
		List<WorldSector> supplyingSectors = worldView
				.getSupplyingSectors(targetUnit);
		if (targetUnit.isNavy()) {
			Optional<WorldSector> port = supplyingSectors.stream()
					.filter(WorldSector::isPort)
					.findFirst();
			if (port.isPresent()) {
				addChild(supplyNode, port.get());
				return true;
			}
		} else if (targetUnit.isLand()) {
			Optional<WorldSector> city = supplyingSectors.stream()
					.filter(s -> s.isPlayerCity() && s.onMyTeam())
					.findFirst();
			if (city.isPresent()) {
				addChild(supplyNode, city.get());
				return true;
			}
		}
		UnitSuppliesUnitPredicate suppliesPredicate = new UnitSuppliesUnitPredicate(targetUnit);
		List<Unit> children = supplyingSectors.stream()
				.map(WorldSector::getTopUnit)
				.filter(u -> suppliesPredicate.test(u))
				.collect(Collectors.toList());
		for (Unit child : children) {
			if (allValues.contains(child)) {
				continue;
			}
			UnitSupplyNode childNode = addChild(supplyNode, child);
			if (huntForCitySupply(worldView, childNode)) {
				return true;
			}
		}
		return false;
	}

	private UnitSupplyNode addChild(SupplyNode parent, Unit child) {
		if (allValues.contains(child)) {
			throw new IllegalStateException("Duplicate node " + child);
		}
		UnitSupplyNode childNode = new UnitSupplyNode(parent, child);
		parent.addChild(childNode);
		allValues.add(child);
		return childNode;
	}

	private void addChild(SupplyNode parent, WorldSector sector) {
		SectorSupplyNode childNode = new SectorSupplyNode(parent, sector);
		parent.addChild(childNode);
	}

	public Iterator<SectorCoordVector> getSupplyChain() {
		List<SectorCoordVector> vectors = new ArrayList<>();
		addChildren(vectors, rootNode);
		return vectors.iterator();
	}

	private void addChildren(List<SectorCoordVector> vectors,
			SupplyNode supplyNode) {
		for (SupplyNode childNode : supplyNode) {
			vectors.add(new SectorCoordVector(supplyNode.getSectorCoords(), childNode.getSectorCoords()));
			addChildren(vectors, childNode);
		}
	}

	public boolean isLinkedToPort() {
		return linkedToPort;
	}
}
