package com.kenstevens.stratinit.ui.adapter;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.SelectionEvent;

import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.site.action.ActionFactory;
import com.kenstevens.stratinit.ui.selection.SelectEvent;
import com.kenstevens.stratinit.ui.shell.TopShell;
import com.kenstevens.stratinit.util.Spring;

public class CancelMoveSelectionAdapter extends StratinitSelectionAdapter {
	private final Logger logger = Logger.getLogger(getClass());

	public CancelMoveSelectionAdapter(SelectEvent selectEvent,
			Spring spring, ActionFactory actionFactory, TopShell topShell) {
		super(selectEvent, spring, actionFactory, topShell);

	}

	public void widgetSelected(final SelectionEvent e) {
		try {
			List<UnitView> units = selectEvent.getSelectedUnits();
			if (units.isEmpty()) {
				return;
			}
			actionFactory.cancelMoveOrder(units);
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
		}
	}
}