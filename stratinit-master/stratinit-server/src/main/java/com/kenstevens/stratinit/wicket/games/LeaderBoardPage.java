package com.kenstevens.stratinit.wicket.games;

import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.kenstevens.stratinit.wicket.BasePage;

public class LeaderBoardPage extends BasePage {
	@SpringBean
	PlayerListProvider playerListProvider;
	
	private static final long serialVersionUID = 1L;

	public LeaderBoardPage() {
		super();
		PlayerListView playerListView = new PlayerListView("players",
				new PlayerListModel(playerListProvider, false), 20);

		add(playerListView);
		add(new PagingNavigator("navigator", playerListView));
	}

}
