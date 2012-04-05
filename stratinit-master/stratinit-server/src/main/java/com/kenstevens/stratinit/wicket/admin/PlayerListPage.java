package com.kenstevens.stratinit.wicket.admin;

import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.kenstevens.stratinit.wicket.base.BasePage;
import com.kenstevens.stratinit.wicket.model.PlayerListModel;
import com.kenstevens.stratinit.wicket.player.PlayerListView;
import com.kenstevens.stratinit.wicket.provider.PlayerListProvider;

public class PlayerListPage extends BasePage {
	private static final long serialVersionUID = 1L;
	@SpringBean
	PlayerListProvider playerListProvider;

	public PlayerListPage(final PageParameters parameters) {
		super(parameters);
		PlayerListView playerListView = new PlayerListView("players",
				new PlayerListModel(playerListProvider, true), 20);

		add(playerListView);
		add(new PagingNavigator("navigator", playerListView));
	}

}
