package com.kenstevens.stratinit.ui.adapter;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.SelectedCity;
import com.kenstevens.stratinit.ui.shell.TopShell;
import com.kenstevens.stratinit.ui.tabs.CedeWindow;
import com.kenstevens.stratinit.ui.tabs.CedeWindowControl;
import com.kenstevens.stratinit.util.Spring;

public class CedeCitySelectionAdapter extends SelectionAdapter {
	private final Logger logger = Logger.getLogger(getClass());
	private final CedeWindow cedeWindow;
	private final Spring spring;
	private final SelectedCity selectedCity;
	private final TopShell topShell;


	public CedeCitySelectionAdapter(SelectedCity selectedCity, Spring spring, CedeWindow cedeWindow, TopShell topShell) {
		this.selectedCity = selectedCity;
		this.spring = spring;
		this.cedeWindow = cedeWindow;
		this.topShell = topShell;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		try {
			City city = selectedCity.getCity();
			if (city == null) {
				return;
			}
			topShell.open(cedeWindow);
			CedeWindowControl cedeWindowController = spring.autowire(new CedeWindowControl( cedeWindow, city ));
			cedeWindowController.setContents();
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
		}
	}
}
