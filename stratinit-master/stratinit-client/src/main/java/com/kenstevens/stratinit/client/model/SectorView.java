package com.kenstevens.stratinit.client.model;

import com.kenstevens.stratinit.dto.SISector;

public class SectorView extends WorldSector {

	private static final long serialVersionUID = 1L;

	public SectorView(SISector sisector) {
		super(new Game(), sisector.coords, sisector.type, sisector.islandId);
		setCityType(sisector.cityType);
		setMyRelation(sisector.myRelation);
		setTheirRelation(sisector.theirRelation);
		setHoldsFriendlyCarrier(sisector.holdsFriendlyCarrier);
		setHoldsMyTransport(sisector.holdsMyTransport);
		setSuppliesLand(sisector.suppliesLand);
		setSuppliesNavy(sisector.suppliesNavy);
		setHoldsMyCapital(sisector.holdsMyCapital);
		setTopUnitType(sisector.topUnitType);
		setFlak(sisector.flak);
		this.setLastSeen(sisector.lastSeen);
		this.setIsland(sisector.islandId);
	}
}
