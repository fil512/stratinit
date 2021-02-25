package com.kenstevens.stratinit.wicket.game;

import com.kenstevens.stratinit.dto.SITeamRank;
import com.kenstevens.stratinit.wicket.model.TeamRankModel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;

public class TeamRankView extends PageableListView<SITeamRank> {

	private static final long serialVersionUID = 1L;

	public TeamRankView(String id, TeamRankModel teamRankModel,
			int itemsPerPage) {
		super(id, teamRankModel, itemsPerPage);
	}

	@Override
	protected void populateItem(ListItem<SITeamRank> listItem) {
		final SITeamRank sITeamRank = listItem.getModelObject();
		listItem.add(new Label("index", ""+(listItem.getIndex()+1)));
		listItem.add(new Label("itemName", sITeamRank.getName()));
		listItem.add(new Label("rank", "" + sITeamRank.getRank().intValue()));
		listItem.add(new Label("victories", "" + sITeamRank.getVictories()));
		listItem.add(new Label("opponents", "" + sITeamRank.getOpponents()));
		listItem.add(new Label("vicperc", "" + sITeamRank.getVicPerc()));
		listItem.add(new Label("won", "" + sITeamRank.getWins()));
		listItem.add(new Label("played", "" + sITeamRank.getPlayed()));
		listItem.add(new Label("winperc", "" + sITeamRank.getWinPerc()));
	}

}
