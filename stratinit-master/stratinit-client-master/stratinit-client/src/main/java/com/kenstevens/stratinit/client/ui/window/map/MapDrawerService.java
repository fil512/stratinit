package com.kenstevens.stratinit.client.ui.window.map;

import com.kenstevens.stratinit.client.model.WorldSector;
import com.kenstevens.stratinit.client.ui.image.ImageLibrary;
import com.kenstevens.stratinit.client.ui.window.LineStyle;
import com.kenstevens.stratinit.type.SectorCoords;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

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

	public void drawUnknownReach(Set<SectorCoords> coords) {
		new UnknownReachDrawer(mapImageManager, coords).draw();
	}
}
