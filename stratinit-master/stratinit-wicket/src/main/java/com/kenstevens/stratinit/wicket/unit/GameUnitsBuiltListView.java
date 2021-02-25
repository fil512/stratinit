package com.kenstevens.stratinit.wicket.unit;

import com.kenstevens.stratinit.wicket.model.GameUnitsBuiltListModel;
import com.kenstevens.stratinit.wicket.provider.GameUnitsBuilt;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;

public class GameUnitsBuiltListView extends PageableListView<GameUnitsBuilt> {

	private static final long serialVersionUID = 1L;

	public GameUnitsBuiltListView(String id,
			GameUnitsBuiltListModel gameUnitsBuiltListModel, int itemsPerPage) {
		super(id, gameUnitsBuiltListModel, itemsPerPage);
	}

	@Override
	protected void populateItem(ListItem<GameUnitsBuilt> listItem) {
		final GameUnitsBuilt gub = listItem.getModelObject();
		int gameId = gub.getGame().getGameId();
		listItem.add(new Label("gamdId", "" + gameId));
		UnitsBuiltListView unitsBuiltListView = new UnitsBuiltListView("unitsBuilt", gub.getUnitsBuilt());
		listItem.add(unitsBuiltListView);
		listItem.add(new GameUnitsChartPanel("gameUnitsChartPanel", gameId));
	}

}
