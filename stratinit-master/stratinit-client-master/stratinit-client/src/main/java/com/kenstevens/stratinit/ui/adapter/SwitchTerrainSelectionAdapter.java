package com.kenstevens.stratinit.ui.adapter;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.SelectionEvent;

import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.site.action.ActionFactory;
import com.kenstevens.stratinit.ui.selection.SelectEvent;
import com.kenstevens.stratinit.ui.shell.TopShell;
import com.kenstevens.stratinit.util.Spring;

public class SwitchTerrainSelectionAdapter extends StratinitSelectionAdapter {
	private final Log logger = LogFactory.getLog(getClass());

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