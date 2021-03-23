package com.kenstevens.stratinit.client.model;

import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.stereotype.Repository;

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
