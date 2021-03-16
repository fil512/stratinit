package com.kenstevens.stratinit.ui.adapter;

import com.kenstevens.stratinit.client.api.IEventSelector;
import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.client.site.action.post.ActionFactory;
import com.kenstevens.stratinit.client.util.Spring;
import com.kenstevens.stratinit.shell.TopShell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MessageBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DisbandSelectionAdapter extends StratinitSelectionAdapter {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public DisbandSelectionAdapter(IEventSelector eventSelector,
								   Spring spring, ActionFactory actionFactory, TopShell topShell) {
		super(eventSelector, spring, actionFactory, topShell);

	}

	public void widgetSelected(final SelectionEvent e) {
		try {
			List<UnitView> units = eventSelector.getSelectedUnits();
			if (units.isEmpty()) {
				return;
			}
			if (acceptDisbandUnit(units)) {
				actionFactory.disbandUnit(units);
			}
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
		}
	}

	private boolean acceptDisbandUnit(List<UnitView> units) {
		MessageBox messageBox = new MessageBox(topShell.getShell(), SWT.OK
				| SWT.CANCEL | SWT.ICON_WARNING);
		if (units.size() == 1) {
			UnitView unit = units.get(0);
				messageBox.setMessage("Are you sure you want to disband "
						+ unit.toMyString() + " in " + unit.getCoords() + "?");
		} else {
			messageBox.setMessage("Are you sure you want to disband "+units.size()+" units in "+units.get(0).getCoords()+"?");
		}
		int button = messageBox.open();
		return button == SWT.OK;
	}

}