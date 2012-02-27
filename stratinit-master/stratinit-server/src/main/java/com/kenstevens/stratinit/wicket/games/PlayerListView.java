package com.kenstevens.stratinit.wicket.games;

import java.text.SimpleDateFormat;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;

import com.kenstevens.stratinit.model.Player;

public class PlayerListView extends PageableListView<Player> {
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat(
			"d MMM yyyy");

	private static final long serialVersionUID = 1L;
	private final PlayerListModel playerListModel;

	public PlayerListView(String id, PlayerListModel playerListModel,
			int itemsPerPage) {
		super(id, playerListModel, itemsPerPage);
		this.playerListModel = playerListModel;
	}

	@Override
	protected void populateItem(ListItem<Player> listItem) {
		final Player player = listItem.getModelObject();
		listItem.add(new Label("name", player.getUsername()));
		if (playerListModel.isAdmin()) {
			listItem.add(new Label("email", "" + player.getEmail()));
			listItem.add(new Label("created", ""
					+ FORMAT.format(player.getCreated())));
			listItem.add(new Label("lastLogin", ""
					+ FORMAT.format(player.getLastLogin())));
		} else {
			listItem.add(new Label("won", "" + player.getWins()));
			listItem.add(new Label("played", "" + player.getPlayed()));
			listItem.add(new Label("winperc", "" + player.getWinPerc()));
		}
	}

}
