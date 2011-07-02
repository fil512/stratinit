package com.kenstevens.stratinit.client.gwt.widget.play.tab;

import com.kenstevens.stratinit.client.gwt.model.GWTSectorCoords;
import com.kenstevens.stratinit.client.gwt.service.GWTDataManager;

public final class GWTMapCentre {
	private static GWTMapCentre instance;
	private final int size;
	private GWTSectorCoords shift = new GWTSectorCoords(0, 0);
	private GWTSectorCoords unshift = new GWTSectorCoords(0, 0);;
	private static boolean initialized = false;

	private GWTMapCentre(int size) {
		this.size = size;
	}
	
	public static void init() {
		// TODO REF this is messy
		if (instance == null) {
			instance = new GWTMapCentre(GWTDataManager.getSize());
		}
		if (GWTDataManager.getNation() == null) {
			return;
		}
		GWTSectorCoords coords = GWTDataManager.getNation().getStartCoords();
		instance.setShifts(coords);
		initialized = true;
	}

	public void setShifts(GWTSectorCoords coords) {
		int midpoint = size / 2;
		shift = new GWTSectorCoords(size, coords.x - midpoint, coords.y - midpoint);
		unshift = new GWTSectorCoords(size, midpoint - coords.x, midpoint
				- coords.y);
	}

	public static GWTSectorCoords getClickShift() {
		if (!initialized) {
			init();
		}
		return instance.shift;
	}

	public static GWTSectorCoords getDrawShift() {
		if (!initialized) {
			init();
		}
		return instance.unshift;
	}
}
