package com.kenstevens.stratinit.wicket.unit;

import org.apache.wicket.markup.html.panel.Panel;


@SuppressWarnings("serial")
public class UnitTablePanel extends Panel {
	public UnitTablePanel(String id, DayUnitsListView dayUnitsListView) {
		super(id);
		add(dayUnitsListView);
	}
}
