package com.kenstevens.stratinit.ui.adapter;

import com.kenstevens.stratinit.client.site.action.post.ActionFactory;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateUnitsSelectionAdapter extends SelectionAdapter {
	final Logger logger = LoggerFactory.getLogger(getClass());
	private final ActionFactory actionFactory;

	public UpdateUnitsSelectionAdapter(ActionFactory actionFactory) {
		this.actionFactory = actionFactory;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		try {
			actionFactory.unitList();
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
		}
	}
}
