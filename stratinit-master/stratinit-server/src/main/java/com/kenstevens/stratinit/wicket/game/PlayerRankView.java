package com.kenstevens.stratinit.wicket.game;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;

import com.kenstevens.stratinit.model.PlayerRank;
import com.kenstevens.stratinit.wicket.model.PlayerRankModel;

public class PlayerRankView extends PageableListView<PlayerRank> {

	private static final long serialVersionUID = 1L;

	public PlayerRankView(String id, PlayerRankModel playerRankModel,
			int itemsPerPage) {
		super(id, playerRankModel, itemsPerPage);
	}

	@Override
	protected void populateItem(ListItem<PlayerRank> listItem) {
		final PlayerRank playerRank = listItem.getModelObject();
		listItem.add(new Label("index", ""+(listItem.getIndex()+1)));
		listItem.add(new PlayerTeamsPanel("itemName", playerRank.getName()));
		listItem.add(new Label("rank", "" + playerRank.getRank().intValue()));
		listItem.add(new Label("victories", "" + playerRank.getVictories()));
		listItem.add(new Label("opponents", "" + playerRank.getOpponents()));
		listItem.add(new Label("won", "" + playerRank.getWins()));
		listItem.add(new Label("played", "" + playerRank.getPlayed()));
		listItem.add(new Label("winperc", "" + playerRank.getWinPerc()));
		listItem.add(new Label("vicperc", "" + playerRank.getVicPerc()));
		
	}

}
