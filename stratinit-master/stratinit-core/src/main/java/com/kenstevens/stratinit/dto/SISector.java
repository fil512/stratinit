package com.kenstevens.stratinit.dto;

import com.kenstevens.stratinit.client.model.WorldSector;
import com.kenstevens.stratinit.type.*;

import java.util.Date;


public class SISector implements StratInitDTO {
    private static final long serialVersionUID = 1L;
    public SectorCoords coords;
    public SectorType type;
    public CityType cityType;
    public int nationId = Constants.UNASSIGNED;
    public Date lastSeen;
    public RelationType myRelation;
    public RelationType theirRelation;
    public boolean holdsFriendlyCarrier = false;
    public boolean holdsMyTransport = false;
	public boolean suppliesLand = false;
	public boolean suppliesNavy = false;
	public boolean holdsMyCapital = false;
	public UnitType topUnitType;
	public int topUnitId = Constants.UNASSIGNED;
	public int flak = 0;
	public int cannons = 0;
	public int islandId;
	
	public SISector() {}
	
	public SISector(WorldSector worldSector) {
		if (worldSector.getNation() != null) {
			nationId = worldSector.getNation().getNationId();
		}
		coords = worldSector.getCoords();
		type = worldSector.getType();
		cityType = worldSector.getCityType();
		myRelation = worldSector.getMyRelation();
		theirRelation = worldSector.getTheirRelation();
		holdsFriendlyCarrier = worldSector.isHoldsFriendlyCarrier();
		holdsMyTransport = worldSector.isHoldsMyTransport();
		suppliesLand = worldSector.isSuppliesLand();
		suppliesNavy = worldSector.isSuppliesNavy();
		holdsMyCapital = worldSector.isHoldsMyCapital();
		topUnitType = worldSector.getTopUnitType();
		if (worldSector.getTopUnit() != null) {
			topUnitId = worldSector.getTopUnit().getId();
		}
		flak = worldSector.getFlak();
		cannons = worldSector.getCannons();
		islandId = worldSector.getIsland();
	}

	@Override
	public String toString() {
		return coords.toString();
	}
}
