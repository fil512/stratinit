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

public class CedeUnitsSelectionAdapter extends StratinitSelectionAdapter {
	private final Log logger = LogFactory.getLog(getClass());
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
