package com.kenstevens.stratinit.ui.adapter;

import com.kenstevens.stratinit.control.selection.SelectEvent;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.shell.TopShell;
import com.kenstevens.stratinit.site.action.post.ActionFactory;
import com.kenstevens.stratinit.util.Spring;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MessageBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BuildCitySelectionAdapter extends StratinitSelectionAdapter {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public BuildCitySelectionAdapter(SelectEvent selectEvent, Spring spring,
									 ActionFactory actionFactory, TopShell topShell) {
		super(selectEvent, spring, actionFactory, topShell);
	}

	public void widgetSelected(final SelectionEvent e) {
		try {
			List<UnitView> units = selectEvent.getSelectedUnits();
			if (units.isEmpty()) {
				return;
			}
			if (acceptBuildCity(units)) {
				actionFactory.buildCity(units);
			}
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
		}
	}

	private boolean acceptBuildCity(List<UnitView> units) {
		MessageBox messageBox = new MessageBox(topShell.getShell(), SWT.OK
				| SWT.CANCEL | SWT.ICON_WARNING);
		if (units.size() == 0) {
			messageBox.setMessage("ERROR:  No unit selected.");
			messageBox.open();
			return false;
		}
		UnitView unit = units.get(0);
		messageBox.setMessage("Are you sure you want to create a new city at "
				+ unit.getCoords() + "?");
		int button = messageBox.open();
		return button == SWT.OK;
	}

}