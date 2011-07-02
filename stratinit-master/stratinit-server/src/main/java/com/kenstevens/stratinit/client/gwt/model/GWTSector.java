package com.kenstevens.stratinit.client.gwt.model;

import java.io.Serializable;


public class GWTSector implements Serializable {
	private static final long serialVersionUID = 1L;
	public GWTSectorCoords coords;
	public GWTSectorType type;
	public GWTCityType cityType;
	public GWTSectorColour sectorColour;
	public GWTUnitType topUnitType;
	public int topUnitId = -1;

	public GWTSector() {
	}

	public GWTSector(int x, int y, GWTSectorType type, GWTSectorColour sectorColour) {
		this.coords = new GWTSectorCoords(x, y);
		this.type = type;
		this.sectorColour = sectorColour;
	}
	

	public boolean containsMyUnit(GWTSectorCoords coords) {
		return sectorColour == GWTSectorColour.aqua &&
				topUnitId > 0;
	}
}
