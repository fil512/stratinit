package com.kenstevens.stratinit.ui.window.map.drawer;

import java.util.Set;

import org.eclipse.swt.graphics.GC;

import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.ui.window.MapImageManager;

public class ReachDrawer extends Drawer {

	private final Set<WorldSector> sectors;

	public ReachDrawer(MapImageManager mapImageManager,
			Set<WorldSector> sectors) {
		super(mapImageManager);
		this.sectors = sectors;
	}

	@Override
	public void drawWithGC(GC gc) {
		mapImageManager.getMapDrawerManager().drawReach(gc, sectors);
	}

}
