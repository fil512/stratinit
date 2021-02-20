package com.kenstevens.stratinit.ui.window;

import com.kenstevens.stratinit.control.selection.MapCentre;
import com.kenstevens.stratinit.main.ClientConstants;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.move.Attack;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.ui.image.ImageLibrary;
import com.kenstevens.stratinit.ui.image.ImageUtils;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmallMapImageManager {
	@Autowired
	private Data db;
	@Autowired
	private ImageLibrary imageLibrary;
	@Autowired
	private MapCentre mapCentre;

	public synchronized void drawSmallImage(GC gc) {
		Image image = buildSmallImage();
		if (image == null) {
			return;
		}
		gc.drawImage(image, 0, 0);
	}

	private synchronized Image buildSmallImage() {
		if (!db.isLoaded() || db.getSelectedGameId() == -1) {
			return null;
		}
		// Lazy init due to unknown board size
		int boardSize = db.getBoardSize();
		Image image = new Image(Display.getCurrent(),
				ClientConstants.IMG_PIXELS_SMALL * boardSize,
				ClientConstants.IMG_PIXELS_SMALL * boardSize);
		GC gc = new GC(image);

		for (int x = 0; x < boardSize; ++x) {
			for (int y = 0; y < boardSize; ++y) {
				drawSmallSector(gc, new SectorCoords(x, y));
			}
		}
		gc.dispose();
		return image;
	}

	private void drawSmallSector(GC gc, SectorCoords coords) {
		WorldSector sector = db.getWorld().getWorldSector(coords);
		Image image = getSmallSectorImage(sector);
		drawSmallSquare(gc, coords, image);
		Image unitImage = getSmallUnitImage(sector);
		if (unitImage != null) {
			Attack attack = new Attack(sector);
			// TODO MAC get this working for the Mac
			if (attack.isAttackable()) {
				unitImage = ImageUtils.blackToColour(new RGB(255, 0, 0),
						unitImage, ClientConstants.IMG_PIXELS_SMALL);
			} else if (sector.isAlly()) {
				unitImage = ImageUtils.blackToColour(new RGB(150, 253, 170),
						unitImage, ClientConstants.IMG_PIXELS_SMALL);
			} else if (sector.isMine()) {
				unitImage = ImageUtils.blackToColour(new RGB(255, 255, 255),
						unitImage, ClientConstants.IMG_PIXELS_SMALL);
			}
			drawSmallSquare(gc, coords, unitImage);
		}
	}

	private Image getSmallSectorImage(Sector sector) {
		Image image = imageLibrary.getSmallBlack();
		if (sector != null) {
			if (sector.isWater()) {
				image = imageLibrary.getSmallBlue();
			} else {
				image = imageLibrary.getSmallGreen();
			}
		}
		return image;
	}

	private Image getSmallUnitImage(WorldSector sector) {
		Image image = null;
		if (sector != null) {
			if (sector.isPlayerCity()) {
				image = imageLibrary.getSmallCity();
			} else if (sector.hasAirUnit()) {
				image = imageLibrary.getSmallAir();
			} else if (sector.hasNavyUnit()) {
				image = imageLibrary.getSmallNavy();
			} else if (sector.hasLandUnit()) {
				image = imageLibrary.getSmallLand();
			}
		}
		return image;
	}

	public synchronized Rectangle getSmallBounds() {
		return new Rectangle(0, 0, ClientConstants.IMG_PIXELS_SMALL
				* db.getBoardSize(), ClientConstants.IMG_PIXELS_SMALL
				* db.getBoardSize());
	}

	private void drawSmallSquare(GC gc, SectorCoords coords, Image image) {
		int size = db.getBoardSize();
		SectorCoords shiftedCoords = new SectorCoords(coords).shift(size,
				mapCentre.getDrawShift());
		int x = shiftedCoords.x * ClientConstants.IMG_PIXELS_SMALL;
		int y = (db.getBoardSize() - shiftedCoords.y - 1)
				* ClientConstants.IMG_PIXELS_SMALL;
		gc.drawImage(image, x, y);
	}

}
