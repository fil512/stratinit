package com.kenstevens.stratinit.wicket.rank;

import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.kenstevens.stratinit.wicket.BasePage;
import com.kenstevens.stratinit.wicket.game.PlayerListModel;
import com.kenstevens.stratinit.wicket.player.PlayerListProvider;
import com.kenstevens.stratinit.wicket.player.PlayerListView;

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
