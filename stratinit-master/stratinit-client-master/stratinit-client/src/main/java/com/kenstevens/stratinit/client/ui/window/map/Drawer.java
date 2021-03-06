package com.kenstevens.stratinit.client.ui.window.map;

import org.eclipse.swt.graphics.GC;


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
