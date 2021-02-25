package com.kenstevens.stratinit.wicket.game;

import com.kenstevens.stratinit.wicket.base.BasePage;
import com.kenstevens.stratinit.wicket.model.PlayerListModel;
import com.kenstevens.stratinit.wicket.player.PlayerListView;
import com.kenstevens.stratinit.wicket.provider.PlayerListProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.spring.injection.annot.SpringBean;

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
