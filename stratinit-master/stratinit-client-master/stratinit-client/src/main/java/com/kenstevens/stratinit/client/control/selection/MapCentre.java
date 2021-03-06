package com.kenstevens.stratinit.client.control.selection;

import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MapCentre {
	@Autowired
	private Data db;
	private boolean initialized;
	private SectorCoords shift = new SectorCoords(0, 0);
	private SectorCoords unshift = new SectorCoords(0, 0);

	public void init() {
		if (!db.isLoaded()) {
			return;
		}
		SectorCoords coords = db.getNation().getStartCoords();
		setShifts(coords);
		initialized = true;
	}

	public void setShifts(SectorCoords coords) {
		if (coords == null) {
			return;
		}
		int size = db.getBoardSize();
		int midpoint = size / 2;
		shift = new SectorCoords(size, coords.x - midpoint, coords.y - midpoint);
		unshift = new SectorCoords(size, midpoint - coords.x, midpoint
				- coords.y);
	}

	public SectorCoords getClickShift() {
		if (!initialized) {
			init();
		}
		return shift;
	}

	public SectorCoords getDrawShift() {
		if (!initialized) {
			init();
		}
		return unshift;
	}
}
