package com.kenstevens.stratinit.wicket.games;

import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.kenstevens.stratinit.wicket.BasePage;

public class TeamRankPage extends BasePage {
	@SpringBean
	GameRankerProvider gameRankerProvider;
	
	private static final long serialVersionUID = 1L;

	public TeamRankPage() {
		super();
		TeamRankView teamRankView = new TeamRankView("teams",
				new TeamRankModel(gameRankerProvider), 20);

		add(teamRankView);
		add(new PagingNavigator("navigator", teamRankView));
	}

}
