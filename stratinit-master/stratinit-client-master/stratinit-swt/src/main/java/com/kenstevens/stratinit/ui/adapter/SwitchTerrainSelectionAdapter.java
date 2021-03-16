package com.kenstevens.stratinit.ui.adapter;

import com.kenstevens.stratinit.client.control.selection.SelectEvent;
import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.client.site.action.post.ActionFactory;
import com.kenstevens.stratinit.client.util.Spring;
import com.kenstevens.stratinit.shell.TopShell;
import org.eclipse.swt.events.SelectionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SwitchTerrainSelectionAdapter extends StratinitSelectionAdapter {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public SwitchTerrainSelectionAdapter(SelectEvent selectEvent, Spring spring,
										 ActionFactory actionFactory, TopShell topShell) {
		super(selectEvent, spring, actionFactory, topShell);
	}

	public void widgetSelected(final SelectionEvent e) {
		try {
			List<UnitView> units = selectEvent.getSelectedUnits();
			if (units.isEmpty()) {
				return;
			}
			actionFactory.switchTerrain(units);
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
		}
	}
}