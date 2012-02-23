package com.kenstevens.stratinit.wicket.games;

import java.util.Map.Entry;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;

import com.kenstevens.stratinit.model.PlayerRank;

public class PlayerRankView extends PageableListView<PlayerRank> {

	private static final long serialVersionUID = 1L;

	public PlayerRankView(String id, PlayerRankModel playerRankModel,
			int itemsPerPage) {
		super(id, playerRankModel, itemsPerPage);
	}

	@Override
	protected void populateItem(ListItem<PlayerRank> listItem) {
		final PlayerRank playerRank = listItem.getModelObject();
		listItem.add(new Label("name", playerRank.getName()));
		listItem.add(new Label("rank", "" + playerRank.getRank().intValue()));
	}

}
