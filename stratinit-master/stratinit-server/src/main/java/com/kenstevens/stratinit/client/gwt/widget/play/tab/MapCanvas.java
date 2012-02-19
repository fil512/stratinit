package com.kenstevens.stratinit.client.gwt.widget.play.tab;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.kenstevens.stratinit.client.gwt.event.GWTCoordsSelectedEventHandler;
import com.kenstevens.stratinit.client.gwt.event.GWTSelectionSource;
import com.kenstevens.stratinit.client.gwt.event.GWTUnitsSelectedEventHandler;
import com.kenstevens.stratinit.client.gwt.model.GWTSector;
import com.kenstevens.stratinit.client.gwt.model.GWTSectorCoords;
import com.kenstevens.stratinit.client.gwt.model.GWTSectorType;
import com.kenstevens.stratinit.client.gwt.model.GWTUnit;
import com.kenstevens.stratinit.client.gwt.service.GWTDataManager;
import com.kenstevens.stratinit.client.gwt.service.UnitMover;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.BrowserEvent;
import com.smartgwt.client.widgets.events.MouseDownEvent;
import com.smartgwt.client.widgets.events.MouseDownHandler;
import com.smartgwt.client.widgets.events.MouseMoveEvent;
import com.smartgwt.client.widgets.events.MouseMoveHandler;
import com.smartgwt.client.widgets.events.RightMouseDownEvent;
import com.smartgwt.client.widgets.events.RightMouseDownHandler;
import com.smartgwt.client.widgets.events.ShowContextMenuEvent;
import com.smartgwt.client.widgets.events.ShowContextMenuHandler;
import com.smartgwt.client.widgets.layout.VLayout;

public class MapCanvas extends Canvas {
	private static final String UNIT_FOLDER = "../../images/unit/";

	private static final String CITY_FOLDER = "../../images/city/";

	private static final String SECTOR_FOLDER = "../../images/sector/";

	private static final String SECTOR_PREFIX = "s";

	private static final String CITY_PREFIX = "c";

	private static final String UNIT_PREFIX = "u";

	private final int mapSize;

	private final VLayout mapLayout;

	private final Label sectorLabel;

	private Img lastActiveImage;

	private final MapCoordConverter mapCoordConverter;

	private final int size;
	
	private final UnitMover unitMover = new UnitMover();
	
	private final Canvas mapCanvas;
	
	// TODO REF this class too big

	public MapCanvas(int size, final VLayout mapLayout, Label sectorLabel) {
		this.size = size;
		this.sectorLabel = sectorLabel;
		this.mapSize = size * GWTConstants.IMG_PIXELS;
		this.mapCoordConverter = new MapCoordConverter(size);
		this.mapLayout = mapLayout;
		this.setWidth(mapSize);
		this.setHeight(mapSize);
		this.setShowEdges(true);
		mapCanvas = new Canvas();
		mapCanvas.setWidth100();
		mapCanvas.setHeight100();
		mapCanvas.setBackgroundColor("grey");
		this.addChild(mapCanvas);
		addHandlers();
	}

	private String getImageString(GWTSectorCoords coords, String prefix, String url) {
		GWTSectorCoords mapCoords = mapCoordConverter.getMapCoords(coords);
		return Canvas.imgHTML(url, GWTConstants.IMG_PIXELS,
				GWTConstants.IMG_PIXELS, prefix+coords.toString(),
				"style=\"position:absolute; left:" + mapCoords.x + "px; top:"
						+ mapCoords.y + "px\" onContextMenu=\"return false;\"", null);
	}

	private void addHandlers() {
		GWTCoordsSelectedEventHandler handler = new GWTCoordsSelectedEventHandler() {

			@Override
			public void coordsSelected(GWTSectorCoords selectedCoords,
					GWTSelectionSource source) {
				selectSector(selectedCoords, source);
			}

		};
		GWTDataManager.addHandler(GWTCoordsSelectedEventHandler.TYPE, handler);
		
		GWTUnitsSelectedEventHandler uhandler = new GWTUnitsSelectedEventHandler() {
			@Override
			public void unitsSelected(List<GWTUnit> selectedUnits, GWTSelectionSource source) {
				if (selectedUnits.isEmpty()) {
					return;
				}
				GWTUnit unit = selectedUnits.get(0);
				selectSector(unit.coords, source);
			}
		};
		GWTDataManager.addHandler(GWTUnitsSelectedEventHandler.TYPE, uhandler);
		
		mapCanvas.addRightMouseDownHandler(new RightMouseDownHandler() {
			public void onRightMouseDown(RightMouseDownEvent event) {
				GWTSectorCoords sectorCoords = mapCoordConverter.getSectorCoords(getMapCoords(event));
				GWTDataManager.setSelectedCoords(sectorCoords, GWTSelectionSource.MAP);
				GWTSector gwtSector = GWTDataManager.getSector(sectorCoords);
				if (gwtSector!= null && gwtSector.containsMyUnit(sectorCoords)) {
					GWTDataManager.setSelectedUnit(gwtSector.topUnitId, GWTSelectionSource.MAP);
				}
			}
		});
		
		mapCanvas.addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				if (!GWTDataManager.initialized()) {
					return;
				}
				GWTSectorCoords sectorCoords = mapCoordConverter.getSectorCoords(getMapCoords(event));
				moveSelectedUnits(sectorCoords);
			}
		});
		
		mapCanvas.addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if (!GWTDataManager.initialized()) {
					return;
				}
				GWTSectorCoords sectorCoords = mapCoordConverter.getSectorCoords(getMapCoords(event));
				sectorLabel.setContents(sectorCoords.toString());
			}
		});
	}


	protected final void moveSelectedUnits(final GWTSectorCoords coords) {
		List<GWTUnit> selectedUnits = GWTDataManager.getSelectedUnits();
		if (selectedUnits.isEmpty()) {
			return;
		}
		unitMover.moveUnits(selectedUnits, coords);
	}
	
	protected final GWTSectorCoords getMapCoords(BrowserEvent<? extends EventHandler> event) {
		return new GWTSectorCoords(event.getX()-this.getPageLeft()-this.getScrollLeft(),
									event.getY()-this.getPageTop()-this.getScrollTop());
	}

	protected void scrollTo(GWTSectorCoords selectedCoords) {
		GWTSectorCoords mapCoords = mapCoordConverter
				.getMapCoords(selectedCoords);
		mapCoords.x = Math.max(0, mapCoords.x - mapLayout.getWidth() / 2);
		mapCoords.y = Math.max(0, mapCoords.y - mapLayout.getHeight() / 2);
		mapLayout.scrollTo(mapCoords.x, mapCoords.y);
	}

	private void markActive(GWTSectorCoords selectedCoords) {
		if (lastActiveImage != null) {
			removeImage(lastActiveImage);
		}
		String filename = SECTOR_FOLDER + "activebox.gif";
		lastActiveImage = addImage(selectedCoords, filename);
	}

	public Img addImage(GWTSectorCoords coords, String filename) {
		
		Img sectorImg = new Img(filename, GWTConstants.IMG_PIXELS,
				GWTConstants.IMG_PIXELS);
		GWTSectorCoords mapCoords = mapCoordConverter.getMapCoords(coords);
		sectorImg.setLeft(mapCoords.x);
		sectorImg.setTop(mapCoords.y);

		sectorImg.addShowContextMenuHandler(new ShowContextMenuHandler() {
			@Override
			public void onShowContextMenu(ShowContextMenuEvent event) {
				event.cancel();
			}
		});

		sectorImg.setParentElement(this);
		sectorImg.draw();
		return sectorImg;
	}

	private void removeImage(Img img) {
		this.removeChild(img);
	}

	public void sectorsChanged(GWTSector[][] sectors) {
		StringBuilder contents = new StringBuilder();
		for (int x = 0; x < size; ++x) {
			for (int y = 0; y < size; ++y) {
				GWTSector sector = sectors[x][y];
				addImage(contents, new GWTSectorCoords(x, y), sector);
			}
		}
		mapCanvas.setContents(contents.toString());
	}

	private void addImage(StringBuilder contents, GWTSectorCoords coords, GWTSector sector) {
		String imgHTML;
		if (sector == null) {
			imgHTML = getImageString(coords, SECTOR_PREFIX, SECTOR_FOLDER + "blank.gif");
			contents.append(imgHTML);
		} else {
			imgHTML = getImageString(coords, SECTOR_PREFIX, getSectorUrl(sector));
			contents.append(imgHTML);
			if (sector.cityType != null) {
				imgHTML = getImageString(coords, CITY_PREFIX, getCityUrl(sector));
				contents.append(imgHTML);
			} else if (sector.topUnitType != null) {
				imgHTML = getImageString(coords, UNIT_PREFIX, getUnitUrl(sector));
				contents.append(imgHTML);
			}
		}
	}

	private String getUnitUrl(GWTSector sector) {
		return UNIT_FOLDER + sector.sectorColour + "/"
				+ sector.topUnitType.toString().toLowerCase() + ".gif";
	}

	private String getCityUrl(GWTSector sector) {
		return CITY_FOLDER + sector.sectorColour + "/"
				+ sector.cityType.toString().toLowerCase() + ".gif";
	}

	private String getSectorUrl(GWTSector sector) {
		String sectorType = sector.type.toString().toLowerCase();
		if (sector.type == GWTSectorType.PLAYER_CITY) {
			sectorType = "land";
		}
		return SECTOR_FOLDER + sectorType + ".gif";
	}

	private void selectSector(GWTSectorCoords selectedCoords,
			GWTSelectionSource source) {
		if (source != GWTSelectionSource.MAP) {
			scrollTo(selectedCoords);
		}
		markActive(selectedCoords);
	}
}
