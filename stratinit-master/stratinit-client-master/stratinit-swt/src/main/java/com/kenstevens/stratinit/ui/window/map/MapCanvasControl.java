package com.kenstevens.stratinit.ui.window.map;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.kenstevens.stratinit.client.api.IEventSelector;
import com.kenstevens.stratinit.client.api.Selection.Source;
import com.kenstevens.stratinit.client.control.MapController;
import com.kenstevens.stratinit.client.control.selection.MapCentre;
import com.kenstevens.stratinit.client.control.selection.SelectNationEvent;
import com.kenstevens.stratinit.client.control.selection.SelectSectorEvent;
import com.kenstevens.stratinit.client.control.selection.SelectUnitsEvent;
import com.kenstevens.stratinit.client.event.GameChangedEvent;
import com.kenstevens.stratinit.client.event.StratinitEventBus;
import com.kenstevens.stratinit.client.event.WorldArrivedEvent;
import com.kenstevens.stratinit.client.main.ClientConstants;
import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.client.site.mover.CityMover;
import com.kenstevens.stratinit.client.site.mover.UnitMover;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.shell.WidgetContainer;
import com.kenstevens.stratinit.supply.Supply;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.ui.tabs.TabManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class MapCanvasControl implements MapController {
	private final Canvas canvas;
	private Point origin = new Point(0, 0);
	private boolean gameSwitched = false;

	@Autowired
	private MapImageManager mapImageManager;
	@Autowired
	private IEventSelector iEventSelector;
	@Autowired
	private UnitMover unitMover;
	@Autowired
	private CityMover cityMover;
	@Autowired
	private SelectedUnits selectedUnits;
	@Autowired
	private Data db;
	@Autowired
	private SelectedCoords selectedCoords;
	@Autowired
	private SelectedCity selectedCity;
	@Autowired
	private Account account;
	@Autowired
	private MapCentre mapCentre;
	@Autowired
	private WidgetContainer widgetContainer;
	@Autowired
	private TabManager tabManager;
	@Autowired
	private SectorDrawerService sectorDrawer;
	@Autowired
	private UnitDrawerService unitDrawer;
	@Autowired
	private StratinitEventBus eventBus;

	// TODO REF this class too big

	public MapCanvasControl(Canvas canvas) {
		this.canvas = canvas;
		setCanvasListeners();
	}

	public void displayActiveUnitsAndScroll(boolean scrollToSeeUnit) {
		displayActiveUnits();
		if (scrollToSeeUnit) {
			scrollToSeeLocation(selectedCoords.getCoords());
		}

		redraw();
	}

	private void displayActiveUnits() {
		mapImageManager.revert();
		if (selectedUnits.isEmpty()) {
			return;
		}
		unitDrawer.drawSelectedUnitRange(new Supply(db.getWorld()));
	}

	private void drawCityMove(City city) {
		sectorDrawer.drawCityMove(city);
	}

	public void setActiveLocation(SectorCoords coords,
			boolean scrollToSeeLocation) {
		mapImageManager.revert();
		sectorDrawer.displayActiveLocation();

		City city = selectedCity.getCity();
		if (city != null) {
			drawCityMove(city);
		}

		if (scrollToSeeLocation) {
			scrollToSeeLocation(coords);
		}

		redraw();
	}
	
	@Subscribe
	public void handleGameChangedEvent(GameChangedEvent event) {
		if (db.getWorld() != null) {
			origin = new Point(0, 0);
			mapImageManager.buildImage();
			gameSwitched = true;
		}
	}
	
	@Subscribe
	public void handleWorldArrivedEvent(WorldArrivedEvent event) {
		buildImage();
	}
	
	@Subscribe
	public void handleSelectSectorEvent(SelectSectorEvent event) {
		setCoords(event.getSelectionSource(), selectedCoords.getCoords());
	}
	
	@Subscribe
	public void handleSelectUnitsEvent(SelectUnitsEvent event) {
		boolean scrollToSeeUnit = false;
		Source source = event.getSelectionSource();
		if (source  == Source.UNIT_TAB) {
			scrollToSeeUnit = true;
		}
		if (widgetContainer.getTabControl().supplyTabSelected()) {
			setCoords(source, selectedCoords.getCoords());
		} else {
			displayActiveUnitsAndScroll(scrollToSeeUnit);
		}
	}
	
	@Subscribe
	public void handleSelectNationEvent(SelectNationEvent event) {
		mapImageManager.buildImage();
		redraw();
	}

	public void setControllers() {
		eventBus.register(this);
	}

	@Override
	public void buildImage() {
		mapImageManager.buildImage();
		if (gameSwitched) {
			setScrollBars();
			gameSwitched = false;
		}

		redraw();
	}

	private void setCoords(Source source,
			SectorCoords coords) {
		boolean scrollToSeeLocation = true;
		if (source == Source.CANVAS_SELECT) {
			scrollToSeeLocation = false;
		}
		setActiveLocation(coords, scrollToSeeLocation);
	}

	private SectorCoords getSectorCoords(int xpoint, int ypoint) {
		int size = db.getBoardSize();
		int x = xpoint / ClientConstants.IMG_PIXELS;
		int y = size - (ypoint / ClientConstants.IMG_PIXELS) - 1;
		SectorCoords retval = new SectorCoords(x, y);
		return retval.shift(size, mapCentre.getClickShift());
	}

	public void scrollToSeeLocation(SectorCoords absCoords) {
		if (absCoords == null) {
			return;
		}
		SectorCoords coords = absCoords.shift(db.getBoardSize(),
				mapCentre.getDrawShift());

		Rectangle mapBounds = mapImageManager.getBounds();
		Rectangle client = canvas.getClientArea();
		if (client.width < mapBounds.width) {
			ScrollBar hBar = canvas.getHorizontalBar();
			int width = client.width / ClientConstants.IMG_PIXELS;
			int newX = (coords.x - width / 2) * ClientConstants.IMG_PIXELS;
			if (newX < 0) {
				newX = 0;
			}
			if (newX + client.width > mapBounds.width) {
				newX = mapBounds.width - client.width;
			}
			int hSelection = newX;
			moveHorizontal(mapBounds, hSelection);
			hBar.setSelection(hSelection);
		}
		if (client.height < mapBounds.height) {
			int height = client.height / ClientConstants.IMG_PIXELS;
			int newY = ((db.getBoardSize() - coords.y - 1) - height / 2)
					* ClientConstants.IMG_PIXELS;
			if (newY < 0) {
				newY = 0;
			}
			if (newY + client.height > mapBounds.height) {
				newY = mapBounds.height - client.height;
			}
			ScrollBar vBar = canvas.getVerticalBar();
			int vSelection = newY;
			moveVertical(mapBounds, vSelection);
			vBar.setSelection(vSelection);
		}
	}

	public final void redraw() {
		Display display = Display.getDefault();

		if (display.isDisposed())
			return;
		display.asyncExec(new Runnable() {
			public void run() {
				if (canvas.isDisposed())
					return;
				canvas.redraw();
			}
		});
	}

	public void setScrollBars() {
		Display display = Display.getDefault();

		if (display.isDisposed())
			return;
		display.asyncExec(new Runnable() {
			public void run() {
				if (canvas.isDisposed())
					return;
				setScrollArea();
			}
		});
	}

	private void moveHorizontal(Rectangle rect, int hSelection) {
		int destX = -hSelection - origin.x;
		canvas.scroll(destX, 0, 0, 0, rect.width, rect.height, false);
		origin.x = -hSelection;
	}

	private void moveVertical(Rectangle rect, int vSelection) {
		int destY = -vSelection - origin.y;
		canvas.scroll(0, destY, 0, 0, rect.width, rect.height, false);
		origin.y = -vSelection;
	}

	// TODO REF this method too complicated
	private void selectSectorOrUnits(SectorCoords sectorCoords) {
        WorldView world = db.getWorld();
        if (world == null) {
            return;
        }
        WorldSector sector = world.getWorldSector(sectorCoords);
        if (sector == null) {
            return;
        }
        boolean switchToSectorTab = true;
        if (!sector.isMine()) {
            if (sector.getNation() == null && account.getPreferences().isCanvasScroll()) {
                iEventSelector.selectSectorCoords(sectorCoords, Source.CANVAS_SELECT_OTHER);
            } else {
                iEventSelector.selectSectorCoords(sectorCoords, Source.CANVAS_SELECT);
            }
		} else {
			if (db.getCity(sectorCoords) != null && tabManager.cityTabSelected()) {
				iEventSelector.selectSectorCoords(sectorCoords,
						Source.CANVAS_SELECT);
				switchToSectorTab = false;
			} else {
				List<UnitView> unitsInSector = Lists.newArrayList();
				UnitList unitList = db.getUnitList();
				if (unitList != null) {
					unitsInSector.addAll(unitList.unitsAt(
							sectorCoords));
				}
				if (unitsInSector.size() == 1) {
					iEventSelector
							.selectUnits(unitsInSector, Source.CANVAS_SELECT);
					if (tabManager.supplyTabSelected() && unitsInSector.get(0).isSupply()) {
						switchToSectorTab = false;
					}
				} else if (sector.isHoldsShipAtSea()) {
					selectShip(unitsInSector);
				} else {
					iEventSelector.selectSectorCoords(sectorCoords,
							Source.CANVAS_SELECT);
				}
			}
		}
		if (switchToSectorTab) {
			widgetContainer.getTabControl().switchToSectorTab();
		}
	}

	private void selectShip(List<UnitView> unitsInSector) {
		for (UnitView unit : unitsInSector) {
			if (unit.isNavy()) {
				iEventSelector.selectUnit(unit, Source.CANVAS_SELECT);
				break;
			}
		}
	}

	private final class CanvasMouseAdapter extends MouseAdapter {
		@Override
		public void mouseDown(MouseEvent e) {
			// Left-click
			SectorCoords coords = getSectorCoords(e.x - origin.x, e.y
					- origin.y);
			MouseClick click = MouseClick.SELECT;

			if (e.button == 1) {
                if (account.getPreferences().isSwitchMouse()) {
                    click = MouseClick.MOVE;
                } else {
                    click = MouseClick.SELECT;
                }
			} else if (e.button == 3) {
				// Right-click
                if (account.getPreferences().isSwitchMouse()) {
                    click = MouseClick.SELECT;
                } else {
                    click = MouseClick.MOVE;
                }
			}
			if (click == MouseClick.MOVE) {
				if (selectedUnits.isEmpty()) {
					City city = selectedCity.getCity();
					if (city != null) {
						cityMover.setCityMove(city, coords);
					}
				} else {
					unitMover.moveSelectedUnits(coords);
				}
			} else if (click == MouseClick.SELECT) {
				selectSectorOrUnits(coords);
			}
		}
	}

	enum MouseClick {
		MOVE, SELECT
	}

	public final void setCanvasListeners() {
		canvas.addMouseListener(new CanvasMouseAdapter());

		final ScrollBar hBar = canvas.getHorizontalBar();
		hBar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				int hSelection = hBar.getSelection();
				moveHorizontal(mapImageManager.getBounds(), hSelection);
			}
		});
		final ScrollBar vBar = canvas.getVerticalBar();
		vBar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				int vSelection = vBar.getSelection();
				moveVertical(mapImageManager.getBounds(), vSelection);
			}

		});
		canvas.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event e) {
				setScrollArea();
				redraw();
			}
		});
		canvas.addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event e) {
				GC gc = e.gc;
				mapImageManager.drawImage(gc, origin.x, origin.y);
				Rectangle rect = mapImageManager.getBounds();
				Rectangle client = canvas.getClientArea();
				int marginWidth = client.width - rect.width;
				if (marginWidth > 0) {
					gc.fillRectangle(rect.width, 0, marginWidth, client.height);
				}
				int marginHeight = client.height - rect.height;
				if (marginHeight > 0) {
					gc.fillRectangle(0, rect.height, client.width, marginHeight);
				}
			}
		});
		canvas.addListener(SWT.MouseMove, new Listener() {
			public void handleEvent(Event e) {
				WorldView world = db.getWorld();
				if (world == null) {
					return;
				}
				SectorCoords coords = getSectorCoords(e.x - origin.x, e.y
						- origin.y);
				updateCoordsAndDistance(world, coords);
			}
		});
		canvas.addListener(SWT.MouseExit, new Listener() {
			public void handleEvent(Event e) {
				widgetContainer.getMapControl()
						.setCanvasCoordsVisibility(false);
			}
		});
		canvas.addListener(SWT.MouseEnter, new Listener() {
			public void handleEvent(Event e) {
				widgetContainer.getMapControl().setCanvasCoordsVisibility(true);
			}
		});
	}

	private void setScrollArea() {
		final ScrollBar hBar = canvas.getHorizontalBar();
		final ScrollBar vBar = canvas.getVerticalBar();

		Rectangle rect = mapImageManager.getBounds();
		Rectangle client = canvas.getClientArea();
		hBar.setMaximum(rect.width);
		vBar.setMaximum(rect.height);
		hBar.setThumb(Math.min(rect.width, client.width));
		vBar.setThumb(Math.min(rect.height, client.height));
		int hPage = rect.width - client.width;
		int vPage = rect.height - client.height;
		int hSelection = hBar.getSelection();
		int vSelection = vBar.getSelection();
		if (hSelection >= hPage) {
			if (hPage <= 0)
				hSelection = 0;
			origin.x = -hSelection;
		}
		if (vSelection >= vPage) {
			if (vPage <= 0)
				vSelection = 0;
			origin.y = -vSelection;
		}
	}

	private void updateCoordsAndDistance(WorldView world, SectorCoords coords) {
		widgetContainer.getMapControl().updateCoordsAndDistance(world, coords,
				selectedCoords);
	}
}
