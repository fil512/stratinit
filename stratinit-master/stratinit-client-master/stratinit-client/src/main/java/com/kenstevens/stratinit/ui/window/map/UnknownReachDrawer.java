package com.kenstevens.stratinit.ui.window.map;

import java.util.Set;

import org.eclipse.swt.graphics.GC;

import com.kenstevens.stratinit.type.SectorCoords;

public class UnknownReachDrawer extends Drawer {

	private final Set<SectorCoords> coords;

	public UnknownReachDrawer(MapImageManager mapImageManager,
			Set<SectorCoords> coords) {
		super(mapImageManager);
		this.coords = coords;
	}

	@Override
	public void drawWithGC(GC gc) {
		mapImageManager.getMapDrawerManager().drawUnknownReach(gc, coords);
	}

}
