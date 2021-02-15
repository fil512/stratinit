package com.kenstevens.stratinit.ui.adapter;

import com.kenstevens.stratinit.control.selection.SelectEvent;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.shell.TopShell;
import com.kenstevens.stratinit.site.action.ActionFactory;
import com.kenstevens.stratinit.util.Spring;
import org.eclipse.swt.events.SelectionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CedeUnitsSelectionAdapter extends StratinitSelectionAdapter {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final CedeWindow cedeWindow;


	public CedeUnitsSelectionAdapter(SelectEvent selectEvent, Spring spring, ActionFactory actionFactory, TopShell topShell, CedeWindow cedeWindow) {
		super(selectEvent, spring, actionFactory, topShell);
		this.cedeWindow = cedeWindow;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		try {
			List<UnitView> units = selectEvent.getSelectedUnits();
			if (units.isEmpty()) {
				return;
			}
			topShell.open(cedeWindow);
			CedeWindowControl cedeWindowController = spring
					.autowire(new CedeWindowControl(cedeWindow, units));
			cedeWindowController.setContents();
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
		}
	}
}
