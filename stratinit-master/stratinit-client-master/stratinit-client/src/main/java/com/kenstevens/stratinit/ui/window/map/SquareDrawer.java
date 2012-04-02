package com.kenstevens.stratinit.ui.window.map;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

import com.kenstevens.stratinit.main.ClientConstants;
import com.kenstevens.stratinit.type.SectorCoords;

public class SquareDrawer extends Drawer {
	private final Image image;
	private final SectorCoords sectorCoords;

	public SquareDrawer(MapImageManager mapImageManager, Image image, SectorCoords sectorCoords) {
		super(mapImageManager);
		this.image = image;
		this.sectorCoords = sectorCoords;
		
	}
	
	@Override
	public void drawWithGC(GC gc) {
		if (sectorCoords == null) {
			return;
		}
		drawSquare(gc, sectorCoords, image);
	}
	
	private void drawSquare(GC gc, SectorCoords coords, Image image) {
		int boardSize = mapImageManager.getBoardSize();
		SectorCoords shiftedCoords = new SectorCoords(coords).shift(boardSize,
				mapImageManager.getDrawShift());
		SectorCoords mapCoords = mapImageManager.getMapCoords(boardSize, shiftedCoords);
		int x = mapCoords.x;
		int y = mapCoords.y;
		gc.drawImage(image, x, y);
		if (xOnEdge(shiftedCoords)) {
			gc.drawImage(image, x + boardSize * ClientConstants.IMG_PIXELS, y);
		}
		if (yOnEdge(boardSize, shiftedCoords)) {
			gc.drawImage(image, x, y + boardSize * ClientConstants.IMG_PIXELS);
		}
		if (xOnEdge(shiftedCoords) && yOnEdge(boardSize, shiftedCoords)) {
			gc.drawImage(image, x + boardSize * ClientConstants.IMG_PIXELS, y
					+ boardSize * ClientConstants.IMG_PIXELS);
		}
	}
	
	private boolean yOnEdge(int boardSize, SectorCoords shiftedCoords) {
		return shiftedCoords.y >= (boardSize - ClientConstants.BOARD_EDGE_SQUARES);
	}

	private boolean xOnEdge(SectorCoords shiftedCoords) {
		return shiftedCoords.x < ClientConstants.BOARD_EDGE_SQUARES;
	}

}
