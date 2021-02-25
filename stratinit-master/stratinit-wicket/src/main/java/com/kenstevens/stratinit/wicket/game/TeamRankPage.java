package com.kenstevens.stratinit.wicket.game;

import com.kenstevens.stratinit.wicket.base.BasePage;
import com.kenstevens.stratinit.wicket.model.TeamRankModel;
import com.kenstevens.stratinit.wicket.provider.GameRankerProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;

public class TeamRankPage extends BasePage {
	@SpringBean
	GameRankerProvider gameRankerProvider;
	
	private static final long serialVersionUID = 1L;

	public TeamRankPage(PageParameters pageParameters) {
		super(pageParameters);
		 StringValue playerName = pageParameters.get(PlayerTeamsPanel.PLAYER_NAME);
		 String title;
		 String playerNameString = null;
		 if (playerName.isNull()) {
			 title = "Team Rank";
		 } else {
			 playerNameString = playerName.toString();
			 title = playerNameString +" Team Ranks";
		 }
		add(new Label("title", title));
		TeamRankView teamRankView = new TeamRankView("rankables",
				new TeamRankModel(gameRankerProvider, playerNameString), 20);
		add(new RankedPanel("rankedPanel", RankedPanel.TEAM, teamRankView));
	}

}
