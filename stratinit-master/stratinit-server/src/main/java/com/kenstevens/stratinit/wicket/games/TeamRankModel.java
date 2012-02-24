package com.kenstevens.stratinit.wicket.games;

import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;

import com.kenstevens.stratinit.model.TeamRank;

public class TeamRankModel extends LoadableDetachableModel<List<TeamRank>> {
	private static final long serialVersionUID = 1L;

	private final TeamRankProvider teamRankProvider;

	public TeamRankModel(TeamRankProvider teamRankProvider) {
		this.teamRankProvider = teamRankProvider;
	}

	@Override
	protected List<TeamRank> load() {
		return teamRankProvider.getTeamRanks();
	}

}
