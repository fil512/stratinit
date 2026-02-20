package com.kenstevens.stratinit.supply;

import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.type.SectorCoords;

import java.util.stream.StreamSupport;

public class UnitSupplyNode extends SupplyNode {
	private final Unit unit;

	public UnitSupplyNode(SupplyNode parent, Unit targetUnit) {
		super(parent);
		this.unit = targetUnit;
	}

	protected void resupply() {
		unit.resupply();
	}

	public void pullSupply() {
		while(!unit.atMaxAmmo() && StreamSupport.stream(this.spliterator(), false)
				.anyMatch(node -> ((UnitSupplyNode) node).hasAmmo())) {
			for (SupplyNode childNode : this) {
				UnitSupplyNode unitNode = (UnitSupplyNode)childNode;
				if (unitNode.hasAmmo()) {
					unit.addAmmo();
					unitNode.unit.decreaseAmmo();
					unitNode.pullSupply();
				}
				if (unit.atMaxAmmo()) {
					break;
				}
			}
		}
	}

	public boolean hasAmmo() {
		return unit.getAmmo() > 0;
	}

	public Unit getUnit() {
		return unit;
	}

	@Override
	public SectorCoords getSectorCoords() {
		return unit.getCoords();
	}
}
