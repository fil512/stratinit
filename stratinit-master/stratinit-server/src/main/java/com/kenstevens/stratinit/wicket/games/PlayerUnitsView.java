package com.kenstevens.stratinit.wicket.games;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

public class PlayerUnitsView extends ListView<PlayerUnitCount> {

	private static final long serialVersionUID = 1L;

	public PlayerUnitsView(String id, PlayerUnitsModel playerUnitsModel) {
		super(id, playerUnitsModel);
	}

	@Override
	protected void populateItem(ListItem<PlayerUnitCount> listItem) {
		final PlayerUnitCount playerUnitCount = listItem.getModelObject();
		listItem.add(new Label("day", ""+(playerUnitCount.getDay()+1)));
		listItem.add(new Label("unitType", playerUnitCount.getUnitType().toString().toLowerCase()));
		listItem.add(new Label("count", "" + playerUnitCount.getCount()));
		listItem.add(new Label("cost", "" + playerUnitCount.getCost()));
		
	}

}
