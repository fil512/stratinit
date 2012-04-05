package com.kenstevens.stratinit.wicket.unit;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.model.PropertyModel;

import com.kenstevens.stratinit.wicket.model.GameUnitsBuiltListModel;
import com.kenstevens.stratinit.wicket.provider.GameUnitsBuilt;
import com.kenstevens.stratinit.wicket.provider.UnitsBuilt;

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
		UnitsBuiltListView unitsBuiltListView = new UnitsBuiltListView(
				"unitsBuilt", new PropertyModel<List<UnitsBuilt>>(
						listItem.getModel(), "unitsBuilt"));
		listItem.add(unitsBuiltListView);
		listItem.add(new GameUnitsChartPanel("gameUnitsChartPanel", gameId));
	}

}
