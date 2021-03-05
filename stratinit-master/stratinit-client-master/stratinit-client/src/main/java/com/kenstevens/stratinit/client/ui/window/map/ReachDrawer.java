package com.kenstevens.stratinit.client.ui.window.map;

import com.kenstevens.stratinit.client.model.WorldSector;
import org.eclipse.swt.graphics.GC;

import java.util.Set;

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
