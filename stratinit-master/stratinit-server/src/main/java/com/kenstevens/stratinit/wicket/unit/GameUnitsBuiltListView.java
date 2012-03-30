package com.kenstevens.stratinit.wicket.unit;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.model.PropertyModel;

public class GameUnitsBuiltListView extends PageableListView<GameUnitsBuilt> {

	private static final long serialVersionUID = 1L;

	public GameUnitsBuiltListView(String id,
			GameUnitsBuiltListModel gameUnitsBuiltListModel, int itemsPerPage) {
		super(id, gameUnitsBuiltListModel, itemsPerPage);
	}

	@Override
	protected void populateItem(ListItem<GameUnitsBuilt> listItem) {
		final GameUnitsBuilt gub = listItem.getModelObject();
		listItem.add(new Label("gamdId", "" + gub.getGame().getGameId()));
		UnitsBuiltListView unitsBuiltListView = new UnitsBuiltListView(
				"unitsBuilt", new PropertyModel<List<UnitsBuilt>>(
						listItem.getModel(), "unitsBuilt"));
		listItem.add(unitsBuiltListView);
	}

}
