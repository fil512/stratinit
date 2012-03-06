package com.kenstevens.stratinit.ui.adapter;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.kenstevens.stratinit.site.action.ActionFactory;

public class UpdateCitiesSelectionAdapter extends SelectionAdapter {
	final Logger logger = Logger.getLogger(getClass());
	private final ActionFactory actionFactory;

	public UpdateCitiesSelectionAdapter(ActionFactory actionFactory) {
		this.actionFactory = actionFactory;
	}
	
	@Override
	public void widgetSelected(final SelectionEvent e) {
		try {
			actionFactory.cityList();
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
		}
	}
}