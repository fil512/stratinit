package com.kenstevens.stratinit.ui.adapter;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.SelectedCity;
import com.kenstevens.stratinit.client.site.action.post.ActionFactory;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CancelCitySelectionAdapter extends SelectionAdapter {
	private final Logger logger = LoggerFactory.getLogger(getClass());
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
