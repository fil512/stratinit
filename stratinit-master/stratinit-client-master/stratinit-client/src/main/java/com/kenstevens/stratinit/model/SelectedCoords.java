package com.kenstevens.stratinit.model;

import org.springframework.stereotype.Repository;

import com.kenstevens.stratinit.type.SectorCoords;

@Repository
public class SelectedCoords {
	private SectorCoords coords = new SectorCoords(0,0);

	public void setCoords(SectorCoords coords) {
		this.coords = coords;
	}

	public SectorCoords getCoords() {
		return coords;
	}
}
