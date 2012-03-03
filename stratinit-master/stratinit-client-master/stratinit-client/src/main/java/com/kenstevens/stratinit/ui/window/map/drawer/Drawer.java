package com.kenstevens.stratinit.ui.window.map.drawer;

import org.eclipse.swt.graphics.GC;

import com.kenstevens.stratinit.ui.window.MapImageManager;

public abstract class Drawer {
	protected final MapImageManager mapImageManager;
	
	public Drawer(MapImageManager mapImageManager) {
		this.mapImageManager = mapImageManager;
	}
	
	public void draw() {
		mapImageManager.drawWithImage(this);
	}

	public void draw(GC gc) {
		drawWithGC(gc);
	}

	public abstract void drawWithGC(GC gc);
}
