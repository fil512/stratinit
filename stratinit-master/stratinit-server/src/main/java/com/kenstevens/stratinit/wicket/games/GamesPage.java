package com.kenstevens.stratinit.wicket.games;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.velocity.GameActiveListProvider;
import com.kenstevens.stratinit.velocity.GameTable;
import com.kenstevens.stratinit.wicket.BasePage;

@SuppressWarnings("serial")
public class GamesPage extends BasePage {
	@SpringBean
	GameActiveListProvider gameActiveListProvider;

	private final class TeamListView extends ListView<SITeam> {

		private TeamListView(String id,
				IModel<? extends List<? extends SITeam>> model) {
			super(id, model);
		}

		@Override
		protected void populateItem(ListItem<SITeam> item) {
			final SITeam team = item.getModelObject();
			item.add(new Label("nation1", team.nation1));
			item.add(new Label("nation2", team.nation2));
			item.add(new Label("score", ""+team.score));
		}
	}

	private final class NationListView extends ListView<SINation> {

		private NationListView(String id,
				IModel<? extends List<? extends SINation>> model) {
			super(id, model);
		}

		@Override
		protected void populateItem(ListItem<SINation> item) {
			final SINation nation = item.getModelObject();
			item.add(new Label("name", nation.name));
			item.add(new Label("cities", ""+nation.cities));
			item.add(new Label("power", ""+nation.power));
		}
	}

	private final class GameListView extends PageableListView<GameTable> {

		private GameListView(String id,
				IModel<? extends List<? extends GameTable>> model,
				int itemsPerPage) {
			super(id, model, itemsPerPage);
		}

		@Override
		public void populateItem(final ListItem<GameTable> listItem)
		{
			final GameTable game = listItem.getModelObject();
			listItem.add(new Label("name", game.getName()));
			listItem.add(new Label("id", ""+game.getId()));
			listItem.add(new Label("ends", ""+game.getEnds()));
			TeamListView teamView = new TeamListView("teams", new PropertyModel<List<SITeam>>(listItem.getModel(), "teams"));
			listItem.add(teamView);
			NationListView nationView = new NationListView("nations", new PropertyModel<List<SINation>>(listItem.getModel(), "nations"));
			listItem.add(nationView);
		}
	}

	public GamesPage() {
		super();
		// FIXME make this depend on page property
//		add(new Label("endsLabel", "ends"));
		// Add table of players
				final PageableListView<GameTable> gameView = new GameListView("games", new PropertyModel<List<GameTable>>(this,
						"games"), 20);
				add(gameView);
				add(new PagingNavigator("navigator", gameView));
	}
	
	public List<GameTable> getGames()
	{
		return gameActiveListProvider.getGameTableList();
	}
}
