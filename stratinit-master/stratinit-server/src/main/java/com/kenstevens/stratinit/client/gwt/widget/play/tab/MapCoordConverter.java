package com.kenstevens.stratinit.client.gwt.widget.play.tab;

import com.kenstevens.stratinit.client.gwt.model.GWTSectorCoords;

public class MapCoordConverter {

	private final int size;

	public MapCoordConverter(int size) {
		this.size = size;
	}

	public GWTSectorCoords getMapCoords(GWTSectorCoords absCoords) {
		GWTSectorCoords coords = absCoords.shift(size, GWTMapCentre.getDrawShift());

		int x = coords.x * GWTConstants.IMG_PIXELS;
		int y = (size - coords.y - 1) * GWTConstants.IMG_PIXELS;
		return new GWTSectorCoords(x, y);
	}

	public GWTSectorCoords getSectorCoords(GWTSectorCoords coords) {
		int x = coords.x / GWTConstants.IMG_PIXELS;
		int y = size - 1 - coords.y / GWTConstants.IMG_PIXELS;
		GWTSectorCoords retval = new GWTSectorCoords(x, y);
		return retval.shift(size, GWTMapCentre.getClickShift());
	}
}
