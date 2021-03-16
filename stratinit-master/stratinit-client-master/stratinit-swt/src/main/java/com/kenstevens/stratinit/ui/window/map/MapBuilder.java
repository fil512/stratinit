package com.kenstevens.stratinit.ui.window.map;

import com.kenstevens.stratinit.supply.Supply;
import org.eclipse.swt.graphics.GC;

public class MapBuilder extends Drawer {

	private final MapDrawerManager mapDrawerManager;
	private Supply supply;

	public MapBuilder(MapImageManager mapImageManager) {
		super(mapImageManager);
		mapDrawerManager = mapImageManager.getMapDrawerManager();
	}

	@Override
	public void drawWithGC(GC gc) {
		supply = mapDrawerManager.buildMapWithGC(gc);
	}

	public Supply getSupply() {
		return supply;
	}

}
