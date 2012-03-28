package com.kenstevens.stratinit.dto;

import java.io.Serializable;
import java.util.Date;

import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.type.UnitType;


public class SISector implements Serializable {
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
