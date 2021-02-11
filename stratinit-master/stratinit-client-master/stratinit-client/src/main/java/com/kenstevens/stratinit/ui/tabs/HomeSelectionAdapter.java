package com.kenstevens.stratinit.ui.tabs;

import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.shell.WidgetContainer;
import com.kenstevens.stratinit.type.SectorCoords;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class HomeSelectionAdapter extends SelectionAdapter {
	final Logger logger = LoggerFactory.getLogger(getClass());

	private final WidgetContainer widgetContainer;
	private final Data db;

	HomeSelectionAdapter(WidgetContainer widgetContainer, Data db) {
		this.widgetContainer = widgetContainer;
		this.db = db;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		try {
			SectorCoords coords = db.getNation().getStartCoords();
			widgetContainer.getMapControl().centreAndScroll(coords);
			widgetContainer.getMapControl().setCentreHomeEnabled(false);
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
		}
	}
}