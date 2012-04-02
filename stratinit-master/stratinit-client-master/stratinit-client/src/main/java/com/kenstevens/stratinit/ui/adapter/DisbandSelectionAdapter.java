package com.kenstevens.stratinit.ui.adapter;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MessageBox;

import com.kenstevens.stratinit.control.selection.SelectEvent;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.shell.TopShell;
import com.kenstevens.stratinit.site.action.ActionFactory;
import com.kenstevens.stratinit.util.Spring;

public class DisbandSelectionAdapter extends StratinitSelectionAdapter {
	private final Log logger = LogFactory.getLog(getClass());

	public DisbandSelectionAdapter(SelectEvent selectEvent,
			Spring spring, ActionFactory actionFactory, TopShell topShell) {
		super(selectEvent, spring, actionFactory, topShell);

	}

	public void widgetSelected(final SelectionEvent e) {
		try {
			List<UnitView> units = selectEvent.getSelectedUnits();
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