package com.kenstevens.stratinit.supply;

import com.kenstevens.stratinit.client.model.Sector;
import com.kenstevens.stratinit.type.SectorCoords;

public class SectorSupplyNode extends SupplyNode {
	private final Sector sector;
	
	public SectorSupplyNode(SupplyNode parent, Sector sector) {
		super(parent);
		this.sector = sector;
	}

	public Sector getSector() {
		return sector;
	}

	@Override
	public SectorCoords getSectorCoords() {
		return sector.getCoords();
	}
}
