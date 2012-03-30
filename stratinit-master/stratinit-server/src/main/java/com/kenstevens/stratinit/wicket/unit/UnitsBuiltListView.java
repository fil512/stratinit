package com.kenstevens.stratinit.wicket.unit;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;

import com.kenstevens.stratinit.server.remote.helper.UnitsBuilt;

public class UnitsBuiltListView extends PageableListView<UnitsBuilt> {

	private static final long serialVersionUID = 1L;

	public UnitsBuiltListView(String id,
			UnitsBuiltListModel unitsBuiltListModel, int itemsPerPage) {
		super(id, unitsBuiltListModel, itemsPerPage);
	}

	@Override
	protected void populateItem(ListItem<UnitsBuilt> listItem) {
		final UnitsBuilt unitsBuilt = listItem.getModelObject();

		listItem.add(new Label("game", "" + unitsBuilt.getGameId()));
		listItem.add(new Label("unitType", "" + unitsBuilt.getType()));
		listItem.add(new Label("built", "" + unitsBuilt.getCount()));
		listItem.add(new Label("love", "" + unitsBuilt.getLove()));
	}

}
