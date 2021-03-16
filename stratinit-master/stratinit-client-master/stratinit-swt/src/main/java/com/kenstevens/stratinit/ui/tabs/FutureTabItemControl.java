package com.kenstevens.stratinit.ui.tabs;

import com.kenstevens.stratinit.client.api.IEventSelector;
import com.kenstevens.stratinit.client.api.Selection.Source;
import com.kenstevens.stratinit.client.control.TopLevelController;
import com.kenstevens.stratinit.client.model.Data;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Scope("prototype")
@Component
public class FutureTabItemControl implements TopLevelController {
	@Autowired
	private Data db;
	@Autowired
	private IEventSelector eventSelector;
	private NextUpEvents events;
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("h:mm a");

	private final FutureTabItem futureTabItem;

	public FutureTabItemControl(FutureTabItem futureTabItem) {
		this.futureTabItem = futureTabItem;
		setTableListeners();
	}

	public void setControllers() {
	}

	public void setContents() {
		events = new NextUpEvents(db);
		Set<Date> times = events.getTimes();
		Table agenda = futureTabItem.getAgenda();
		agenda.removeAll();
		for (Date time : times) {
			 TableItem item = new TableItem(agenda, SWT.NONE);
			 item.setText(FORMAT.format(time));
			 item.setData(time);
		}
	}
	private void drawCreateBar(Event event, int maxCount, int createEvents) {
		if (createEvents == 0) {
			return;
		}
		Display display = Display.getDefault();
		GC gc = event.gc;
		Color foreground = gc.getForeground();
		Color background = gc.getBackground();
		gc.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
		gc.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		TableColumn eventsTableColumn = futureTabItem.getEventsTableColumn();

		int width = (eventsTableColumn.getWidth() - 1) * createEvents / maxCount;
		gc.fillGradientRectangle(event.x, event.y, width, event.height, true);
		Rectangle rect = new Rectangle(event.x, event.y, width-1, event.height-1);
		gc.drawRectangle(rect);
		gc.setForeground(display.getSystemColor(SWT.COLOR_LIST_FOREGROUND));
		String text = ""+createEvents;
		Point size = event.gc.textExtent(text);
		int offset = Math.max(0, (event.height - size.y) / 2);
		gc.drawText(text, event.x+2, event.y+offset, true);
		gc.setForeground(background);
		gc.setBackground(foreground);
	}

	private void drawMoveBar(Event event, int maxCount, int createEvents, int moveEvents) {
		if (moveEvents == 0) {
			return;
		}
		Display display = Display.getDefault();
		GC gc = event.gc;
		Color foreground = gc.getForeground();
		Color background = gc.getBackground();
		gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
		gc.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		TableColumn eventsTableColumn = futureTabItem.getEventsTableColumn();
		int createWidth = (eventsTableColumn.getWidth() - 1) * createEvents / maxCount;
		int moveWidth = (eventsTableColumn.getWidth() - 1) * moveEvents / maxCount;
		gc.fillGradientRectangle(event.x+createWidth, event.y, moveWidth, event.height, true);
		Rectangle rect = new Rectangle(event.x+createWidth, event.y, moveWidth-1, event.height-1);
		gc.drawRectangle(rect);
		gc.setForeground(display.getSystemColor(SWT.COLOR_LIST_FOREGROUND));
		String text = ""+moveEvents;
		Point size = event.gc.textExtent(text);
		int offset = Math.max(0, (event.height - size.y) / 2);
		gc.drawText(text, event.x+createWidth+2, event.y+offset, true);
		gc.setForeground(background);
		gc.setBackground(foreground);
	}
	private void setTableListeners() {
		final Table agenda = futureTabItem.getAgenda();
		agenda.addListener(SWT.PaintItem, event -> {
			if (event.index == 1) {
				TableItem item = (TableItem) event.item;
				Date time = (Date) item.getData();
				int maxCount = events.getMaxCount();
				int createEvents = events.getCreateEventCount(time);
				int moveEvents = events.getMoveEventCount(time);
				drawCreateBar(event, maxCount, createEvents);
				drawMoveBar(event, maxCount, createEvents, moveEvents);
			}
		});
		agenda.addListener(SWT.Selection, event -> {
			TableItem[] items = agenda.getSelection();
			if (items == null || items.length != 1) {
				return;
			}
			TableItem selectedItem = items[0];
			populateUnitTable(selectedItem);
		});
		final Table units = futureTabItem.getUnits();
		units.addListener(SWT.Selection, event -> {
			TableItem[] items = units.getSelection();
			if (items == null || items.length != 1) {
				return;
			}
			TableItem selectedItem = items[0];
			UnitEvent unitEvent = (UnitEvent) selectedItem.getData();
			if (unitEvent == null) {
				return;
			}
			eventSelector.selectSectorCoords(unitEvent.getCoords(),
					Source.FUTURE_TAB);

		});

	}

	private void populateUnitTable(TableItem selectedItem) {
		Date date = (Date) selectedItem.getData();
		if (date == null) {
			return;
		}
		List<UnitEvent> unitEventList = events.getUnitEventList(date);
		Table units = futureTabItem.getUnits();
		units.removeAll();
		for (UnitEvent unitEvent : unitEventList) {
			TableItem tableItem = new TableItem(units, SWT.NONE);
			tableItem.setData(unitEvent);
			tableItem.setText(new String[] {FORMAT.format(unitEvent.getTime()), unitEvent.getCoords().toString(),
					unitEvent.getUnitType().toString(), unitEvent.getMoveString(), unitEvent.getEventType().toString()});
		}
	}
}
