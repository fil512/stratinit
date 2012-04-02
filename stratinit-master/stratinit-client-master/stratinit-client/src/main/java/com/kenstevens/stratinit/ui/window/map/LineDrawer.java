package com.kenstevens.stratinit.ui.window.map;

import org.eclipse.swt.graphics.GC;

import com.kenstevens.stratinit.main.ClientConstants;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.ui.window.LineStyle;

public class LineDrawer extends Drawer {

	private final SectorCoords start;
	private final SectorCoords end;
	private final LineStyle lineStyle;

	public LineDrawer(MapImageManager mapImageManager, SectorCoords start, SectorCoords end, LineStyle lineStyle) {
		super(mapImageManager);
		this.start = start;
		this.end = end;
		this.lineStyle = lineStyle;
	}

	@Override
	public void drawWithGC(GC gc) {
		drawLine(gc, start, end, lineStyle);
	}
	
	public void drawLine(GC gc, SectorCoords start, SectorCoords end,
			LineStyle lineStyle) {
		int boardSize = mapImageManager.getBoardSize();
		SectorCoords startShiftedCoords = new SectorCoords(start).shift(
				boardSize, mapImageManager.getDrawShift());
		SectorCoords startMapCoords = mapImageManager.getMapCoords(boardSize, startShiftedCoords);
		SectorCoords endShiftedCoords = new SectorCoords(end).shift(boardSize,
				mapImageManager.getDrawShift());
		SectorCoords endMapCoords = mapImageManager.getMapCoords(boardSize, endShiftedCoords);
		lineStyle.apply(gc);
		int half = ClientConstants.IMG_PIXELS / 2;
		gc.drawLine(startMapCoords.x + half, startMapCoords.y + half,
				endMapCoords.x + half, endMapCoords.y + half);
	}

}
