package com.kenstevens.stratinit.ui.window.map;

import java.util.Set;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.ui.image.ImageLibrary;
import com.kenstevens.stratinit.ui.window.LineStyle;
import com.kenstevens.stratinit.ui.window.MapImageManager;
import com.kenstevens.stratinit.ui.window.map.drawer.LineDrawer;
import com.kenstevens.stratinit.ui.window.map.drawer.MapBuilder;
import com.kenstevens.stratinit.ui.window.map.drawer.ReachDrawer;
import com.kenstevens.stratinit.ui.window.map.drawer.SquareDrawer;

@Service
public class MapDrawerService {
	@Autowired
	MapImageManager mapImageManager;
	@Autowired
	ImageLibrary imageLibrary;

	public void drawSquare(Image image, SectorCoords coords) {
		new SquareDrawer(mapImageManager, image, coords).draw();
	}
	
	protected void drawLine(SectorCoords start, SectorCoords end,
			LineStyle lineStyle) {
		new LineDrawer(mapImageManager, start, end, lineStyle).draw();
	}

	public void drawSquare(GC gc, SectorCoords coords, Image image) {
		new SquareDrawer(mapImageManager, image, coords).draw(gc);
	}

	public void drawLine(GC gc, SectorCoords start, SectorCoords end,
			LineStyle lineStyle) {
		new LineDrawer(mapImageManager, start, end, lineStyle).draw(gc);
	}

	public void drawReach(Set<WorldSector> sectors) {
		new ReachDrawer(mapImageManager, sectors).draw();
	}
	
	public void buildMap() {
		new MapBuilder(mapImageManager).draw();
	}
}
