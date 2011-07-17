package com.kenstevens.stratinit.wicket.games;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.wicket.BasePage;

public class LeaderBoardPage extends BasePage {
	@SpringBean
	PlayerDao playerDao;
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("serial")
	public LeaderBoardPage() {
		super();
		// Add table of players
				final PageableListView<Player> playerView;
				add(playerView = new PageableListView<Player>("players", new PlayerListModel(this), 20)
				{
					@Override
					public void populateItem(final ListItem<Player> listItem)
					{
						final Player player = listItem.getModelObject();
						listItem.add(new Label("name", player.getUsername()));
						listItem.add(new Label("won", ""+player.getWins()));
						listItem.add(new Label("played", ""+player.getPlayed()));
						listItem.add(new Label("winperc", ""+player.getWinPerc()));
					}
				});
				add(new PagingNavigator("navigator", playerView));
	}
	
	public List<Player> getPlayers()
	{
		List<Player> players = playerDao.getAllPlayers();
		Comparator<Player> byWon = new Comparator<Player>() {

			@Override
			public int compare(Player player1, Player player2) {
				return Integer.valueOf(player2.getWins()).compareTo(player1.getWins());
			}
			
		};
		Collections.sort(players, byWon);
		return players;
	}
}
