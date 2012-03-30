package com.kenstevens.stratinit.wicket.components;

import org.apache.wicket.markup.html.panel.Panel;

import com.kenstevens.stratinit.wicket.unit.DayUnitsListView;

@SuppressWarnings("serial")
public class UnitTablePanel extends Panel {
	public UnitTablePanel(String id, DayUnitsListView dayUnitsListView) {
		super(id);
		add(dayUnitsListView);
	}
}
