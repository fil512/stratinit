package com.kenstevens.stratinit.wicket.games;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;

import com.kenstevens.stratinit.model.TeamRank;

public class TeamRankView extends PageableListView<TeamRank> {

	private static final long serialVersionUID = 1L;

	public TeamRankView(String id, TeamRankModel teamRankModel,
			int itemsPerPage) {
		super(id, teamRankModel, itemsPerPage);
	}

	@Override
	protected void populateItem(ListItem<TeamRank> listItem) {
		final TeamRank teamRank = listItem.getModelObject();
		listItem.add(new Label("name", teamRank.getName()));
		listItem.add(new Label("rank", "" + teamRank.getRank().intValue()));
		listItem.add(new Label("won", "" + teamRank.getWins()));
		listItem.add(new Label("played", "" + teamRank.getPlayed()));
		listItem.add(new Label("winperc", "" + teamRank.getWinPerc()));
	}

}
