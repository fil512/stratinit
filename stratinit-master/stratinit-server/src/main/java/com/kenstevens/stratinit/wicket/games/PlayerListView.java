package com.kenstevens.stratinit.wicket.games;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;

import com.kenstevens.stratinit.model.Player;

public class PlayerListView extends PageableListView<Player> {

	private static final long serialVersionUID = 1L;

	public PlayerListView(String id, PlayerListModel playerListModel,
			int itemsPerPage) {
		super(id, playerListModel, itemsPerPage);
	}

	@Override
	protected void populateItem(ListItem<Player> listItem) {
		final Player player = listItem.getModelObject();
		listItem.add(new Label("name", player.getUsername()));
		listItem.add(new Label("won", "" + player.getWins()));
		listItem.add(new Label("played", "" + player.getPlayed()));
		listItem.add(new Label("winperc", "" + player.getWinPerc()));
	}

}
