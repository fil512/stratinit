package com.kenstevens.stratinit.client.ui.window.map;

import com.kenstevens.stratinit.client.control.selection.MapCentre;
import com.kenstevens.stratinit.client.main.ClientConstants;
import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.supply.Supply;
import com.kenstevens.stratinit.type.SectorCoords;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class MapImageManager {
	@Autowired
	private Data db;

	@Autowired
	private MapImageBuilderService mapImageBuilder;
	@Autowired
	private MapDrawerManager mapDrawerManager;
	@Autowired
	MapCentre mapCentre;

	private final Map<Integer, Image> images = new Hashtable<>();

	private Image defaultImage;

	private final Lock lock = new ReentrantLock();

	private Rectangle bounds = new Rectangle(0, 0, boardSize(ClientConstants.DEFAULT_BOARD_SIZE), boardSize(ClientConstants.DEFAULT_BOARD_SIZE));

	private int boardSize(int size) {
		return ClientConstants.IMG_PIXELS
				* (size + ClientConstants.BOARD_EDGE_SQUARES);
	}

	private final Map<Integer, Image> lastImages = new Hashtable<>();

	public MapImageManager() {
	}

	public void buildImage() {
		if (!db.isLoaded() || db.getSelectedGameId() == -1) {
			return;
		}

		final Display display = Display.getDefault();

		if (display.isDisposed()) {
			return;
		}

		display.asyncExec(this::buildImageBackground);
	}

	private void buildImageBackground() {
		try {
			lock.lock();
			// Lazy init due to unknown board size
			int boardSize = db.getBoardSize();
			if (images.get(db.getSelectedGameId()) == null) {
				setBounds(new Rectangle(0, 0, boardSize(boardSize), boardSize(boardSize)));
				Image image = new Image(Display.getCurrent(), getBounds());
				putImage(image);
			}
			Supply supply = mapImageBuilder.buildMapImage();
			if (getLastImage() != null) {
				getLastImage().dispose();
			}
			Image imageCopy = new Image(Display.getCurrent(), getImage(),
					SWT.IMAGE_COPY);
			putLastImage(imageCopy);
			mapImageBuilder.buildTopLayer(supply);
		} finally {
			lock.unlock();
		}
	}

	public void revert() {
		try {
			lock.lock();
			if (getLastImage() == null) {
				return;
			}
			if (getImage() != null && getImage() != getDefaultImage()) {
				getImage().dispose();
			}
			putImage(new Image(Display.getCurrent(), getLastImage(),
					SWT.IMAGE_COPY));
		} finally {
			lock.unlock();
		}
	}

	private Image getDefaultImage() {
		if (defaultImage == null) {
			defaultImage = new Image(Display.getCurrent(),
					boardSize(ClientConstants.DEFAULT_BOARD_SIZE),
					boardSize(ClientConstants.DEFAULT_BOARD_SIZE));
		}
		return defaultImage;
	}

	private Image getImage() {
		if (db.getSelectedGameId() == -1
				|| images.get(db.getSelectedGameId()) == null) {
			return getDefaultImage();
		}
		return images.get(db.getSelectedGameId());
	}

	private Image getLastImage() {
		return lastImages.get(db.getSelectedGameId());
	}

	private Image putLastImage(Image imageCopy) {
		return lastImages.put(db.getSelectedGameId(), imageCopy);
	}

	private void putImage(Image image) {
		images.put(db.getSelectedGameId(), image);
	}

	public Rectangle getBounds() {
		return bounds;
	}

	private void setBounds(Rectangle bounds) {
		this.bounds = bounds;		
	}

	public void drawImage(GC gc, int x, int y) {
		boolean locked = false;
		try {
			locked = lock.tryLock();
			if (locked) {
				gc.drawImage(getImage(), x, y);
			}
		} finally {
			if (locked) {
				lock.unlock();
			}
		}
	}

	public void drawWithImage(Drawer drawer) {
		try {
			lock.lock();
			GC gc = new GC(getImage());
			drawer.drawWithGC(gc);
			gc.dispose();
		} finally {
			lock.unlock();
		}
	}

	public int getBoardSize() {
		return db.getBoardSize();
	}

	public SectorCoords getDrawShift() {
		return mapCentre.getDrawShift();
	}

	public SectorCoords getMapCoords(int boardSize, SectorCoords shiftedCoords) {
		int x = shiftedCoords.x * ClientConstants.IMG_PIXELS;
		int y = (boardSize - shiftedCoords.y - 1) * ClientConstants.IMG_PIXELS;
		return new SectorCoords(x, y);
	}

	public MapDrawerManager getMapDrawerManager() {
		return mapDrawerManager;
	}

}
