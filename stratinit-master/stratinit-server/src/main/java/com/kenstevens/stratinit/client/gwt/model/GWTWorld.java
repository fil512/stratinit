package com.kenstevens.stratinit.client.gwt.model;

import java.util.List;

public class GWTWorld {
	private GWTSector[][] sectors;

	public GWTWorld(int size) {
		sectors = new GWTSector[size][size];
	}

	public void setSectors(List<GWTSector> result) {
		for (GWTSector sector : result) {
			sectors[sector.coords.x][sector.coords.y] = sector;
		}
	}

	public GWTSector[][] getSectors() {
		return sectors;
	}

	public GWTSector getSector(GWTSectorCoords coords) {
		return sectors[coords.x][coords.y];
	}
}
