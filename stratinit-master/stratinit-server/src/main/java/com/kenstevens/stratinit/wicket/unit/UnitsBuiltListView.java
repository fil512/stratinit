package com.kenstevens.stratinit.wicket.unit;

import com.kenstevens.stratinit.wicket.provider.UnitsBuilt;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.List;

public class UnitsBuiltListView extends ListView<UnitsBuilt> {

	private static final long serialVersionUID = 1L;

	public UnitsBuiltListView(String id, List<UnitsBuilt> unitsBuiltListModel) {
		super(id, unitsBuiltListModel);
	}

	@Override
	protected void populateItem(ListItem<UnitsBuilt> listItem) {
		final UnitsBuilt unitsBuilt = listItem.getModelObject();

		listItem.add(new Label("unitType", "" + unitsBuilt.getType()));
		listItem.add(new Label("built", "" + unitsBuilt.getCount()));
		listItem.add(new Label("love", "" + unitsBuilt.getLove()));
	}

}
