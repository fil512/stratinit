package com.kenstevens.stratinit.ui.adapter;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.ui.selection.SelectEvent;
import com.kenstevens.stratinit.ui.shell.WidgetContainer;

final public class CentreHomeSelectionAdapter extends SelectionAdapter {
	final Logger logger = Logger.getLogger(getClass());

	private final WidgetContainer widgetContainer;
	private final SelectEvent selectEvent;

	public CentreHomeSelectionAdapter(WidgetContainer widgetContainer,
			SelectEvent selectEvent) {
				this.widgetContainer = widgetContainer;
				this.selectEvent = selectEvent;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		try {
			List<UnitView> units = selectEvent.getSelectedUnits();
			if (units.isEmpty()) {
				return;
			}
			SectorCoords coords = units.get(0).getCoords();
			widgetContainer.getMapControl().centreAndScroll(coords);
			widgetContainer.getMapControl().setCentreHomeEnabled(true);
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
		}
	}
}