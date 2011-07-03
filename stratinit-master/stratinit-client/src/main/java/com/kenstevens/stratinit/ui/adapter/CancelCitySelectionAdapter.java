package com.kenstevens.stratinit.ui.adapter;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.SelectedCity;
import com.kenstevens.stratinit.site.action.ActionFactory;

public class CancelCitySelectionAdapter extends SelectionAdapter {
	private final Logger logger = Logger.getLogger(getClass());
	private final SelectedCity selectedCity;
	private final ActionFactory actionFactory;


	public CancelCitySelectionAdapter(SelectedCity selectedCity, ActionFactory actionFactory) {
		this.selectedCity = selectedCity;
		this.actionFactory = actionFactory;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		try {
			City city = selectedCity.getCity();
			if (city == null) {
				return;
			}
			actionFactory.setCityMove(city, null);
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
		}
	}
}
