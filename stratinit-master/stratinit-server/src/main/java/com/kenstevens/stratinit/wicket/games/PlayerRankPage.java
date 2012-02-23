package com.kenstevens.stratinit.wicket.games;

import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.kenstevens.stratinit.wicket.BasePage;

public class PlayerRankPage extends BasePage {
	@SpringBean
	PlayerRankProvider playerRankProvider;
	
	private static final long serialVersionUID = 1L;

	public PlayerRankPage() {
		super();
		PlayerRankView playerRankView = new PlayerRankView("teams",
				new PlayerRankModel(playerRankProvider), 20);

		add(playerRankView);
		add(new PagingNavigator("navigator", playerRankView));
	}

}
