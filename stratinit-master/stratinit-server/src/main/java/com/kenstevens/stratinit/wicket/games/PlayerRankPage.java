package com.kenstevens.stratinit.wicket.games;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.kenstevens.stratinit.wicket.BasePage;
import com.kenstevens.stratinit.wicket.components.RankedPanel;

public class PlayerRankPage extends BasePage {
	@SpringBean
	GameRankerProvider gameRankerProvider;
	
	private static final long serialVersionUID = 1L;

	public PlayerRankPage() {
		super();
		add(new BookmarkablePageLink<Page>("TeamRankPage2", TeamRankPage.class));
		add(new BookmarkablePageLink<Page>("LeaderBoardPage", LeaderBoardPage.class));
		PlayerRankView playerRankView = new PlayerRankView("rankables",
				new PlayerRankModel(gameRankerProvider), 20);
		add(new RankedPanel("rankedPanel", RankedPanel.PLAYER, playerRankView));
	}

}
