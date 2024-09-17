package com.kenstevens.stratinit.client.ui.adapter;

import com.kenstevens.stratinit.client.api.IEventSelector;
import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.client.shell.TopShell;
import com.kenstevens.stratinit.client.site.action.post.ActionFactory;
import com.kenstevens.stratinit.client.util.Spring;
import org.eclipse.swt.events.SelectionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CedeUnitsSelectionAdapter extends StratinitSelectionAdapter {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final CedeWindow cedeWindow;


	public CedeUnitsSelectionAdapter(IEventSelector eventSelector, Spring spring, ActionFactory actionFactory, TopShell topShell, CedeWindow cedeWindow) {
		super(eventSelector, spring, actionFactory, topShell);
		this.cedeWindow = cedeWindow;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		try {
			List<UnitView> units = eventSelector.getSelectedUnits();
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
