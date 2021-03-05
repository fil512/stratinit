package com.kenstevens.stratinit.client.ui.adapter;

import com.kenstevens.stratinit.client.control.selection.SelectEvent;
import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.client.shell.TopShell;
import com.kenstevens.stratinit.client.site.action.post.ActionFactory;
import com.kenstevens.stratinit.client.util.Spring;
import org.eclipse.swt.events.SelectionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CancelMoveSelectionAdapter extends StratinitSelectionAdapter {
	private final Logger logger = LoggerFactory.getLogger(getClass());

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