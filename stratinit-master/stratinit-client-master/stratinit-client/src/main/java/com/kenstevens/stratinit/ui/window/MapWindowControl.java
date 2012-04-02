package com.kenstevens.stratinit.ui.window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.control.selection.MapCentre;
import com.kenstevens.stratinit.main.ClientConstants;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.shell.WidgetContainer;
import com.kenstevens.stratinit.type.SectorCoords;

@Scope("prototype")
@Component
public class MapWindowControl {
	private final Canvas canvas;

	@Autowired
	private SmallMapImageManager smallMapImageManager;
	@Autowired
	private MapCentre mapCentre;
	@Autowired
	private Data db;
	@Autowired
	private WidgetContainer widgetContainer;

	public MapWindowControl(Canvas canvas) {
		this.canvas = canvas;
		setCanvasListeners();
	}

	public void redraw() {
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

	private SectorCoords getSectorCoords(int xpoint, int ypoint) {
		int size = db.getBoardSize();
		int x = xpoint / ClientConstants.IMG_PIXELS_SMALL;
		int y = size - (ypoint / ClientConstants.IMG_PIXELS_SMALL) - 1;
		SectorCoords retval = new SectorCoords(x, y);
		return retval.shift(size, mapCentre.getClickShift());
	}

	public final void setCanvasListeners() {
		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {

				SectorCoords coords = getSectorCoords(e.x , e.y);
				if (e.button == 1) {
					canvas.redraw();
					widgetContainer.getMapControl().centreMap(coords);
				}
			}

		});

		canvas.addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event e) {
				GC gc = e.gc;
				smallMapImageManager.drawSmallImage(gc);
				Rectangle rect = smallMapImageManager.getSmallBounds();
				Rectangle client = canvas.getClientArea();
				int marginWidth = client.width - rect.width;
				if (marginWidth > 0) {
					gc.fillRectangle(rect.width, 0, marginWidth, client.height);
				}
				int marginHeight = client.height - rect.height;
				if (marginHeight > 0) {
					gc
							.fillRectangle(0, rect.height, client.width,
									marginHeight);
				}
			}
		});
	}

}
