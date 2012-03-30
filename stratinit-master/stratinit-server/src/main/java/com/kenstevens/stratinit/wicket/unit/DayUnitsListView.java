package com.kenstevens.stratinit.wicket.unit;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

@SuppressWarnings("serial")
public class DayUnitsListView extends ListView<DayUnitsListRow> {

	public DayUnitsListView(String id, DayUnitsModel dayUnitsModel) {
		super(id, dayUnitsModel);
	}

	@Override
	protected void populateItem(ListItem<DayUnitsListRow> item) {
		final DayUnitsListRow dayUnitsListRow = item.getModelObject();
		item.add(new Label("type", dayUnitsListRow.getUnitType()));
		item.add(new ListView<String>("unitCell", dayUnitsListRow.getValues()) {
			protected void populateItem(ListItem<String> item) {
				String value = item.getModelObject();
				item.add(new Label("data", value));
			}
		});

	}

}
