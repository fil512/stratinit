package com.kenstevens.stratinit.client.ui.adapter;

import com.kenstevens.stratinit.client.api.IEventSelector;
import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.client.shell.WidgetContainer;
import com.kenstevens.stratinit.type.SectorCoords;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

final public class CentreHomeSelectionAdapter extends SelectionAdapter {
	final Logger logger = LoggerFactory.getLogger(getClass());

	private final WidgetContainer widgetContainer;
	private final IEventSelector iEventSelector;

	public CentreHomeSelectionAdapter(WidgetContainer widgetContainer,
									  IEventSelector eventSelector) {
		this.widgetContainer = widgetContainer;
		this.iEventSelector = eventSelector;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		try {
			List<UnitView> units = iEventSelector.getSelectedUnits();
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