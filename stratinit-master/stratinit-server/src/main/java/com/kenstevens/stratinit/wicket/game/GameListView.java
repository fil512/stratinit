package com.kenstevens.stratinit.wicket.game;

import com.kenstevens.stratinit.wicket.message.MessageBoardPage;
import com.kenstevens.stratinit.wicket.provider.GameTable;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.List;

final class GameListView extends ListView<GameTable> {

	private static final long serialVersionUID = 1L;

	GameListView(String id, List<GameTable> model) {
		super(id, model);
	}

	@Override
	public void populateItem(final ListItem<GameTable> listItem) {
		final GameTable game = listItem.getModelObject();
		listItem.add(new Label("name", game.getName()));
		listItem.add(new Label("id", "" + game.getId()));
		listItem.add(new Label("ends", "" + game.getEnds()));
		TeamListView teamView = new TeamListView("teams", game.getTeams());
		listItem.add(teamView);
		NationListView nationView = new NationListView("nations", game.getNations(), game.hasEnded());
		GamePlayerTablePanel playerTable = new GamePlayerTablePanel("gamePlayerTablePanel", nationView);
		listItem.add(playerTable);
		BookmarkablePageLink<MessageBoardPage> messageBoardLink = getMessageBoardLink(game);
		listItem.add(messageBoardLink);
	}

	private BookmarkablePageLink<MessageBoardPage> getMessageBoardLink(
			final GameTable game) {
		BookmarkablePageLink<MessageBoardPage> messageBoardLink = new BookmarkablePageLink<MessageBoardPage>(
				"messageBoardLink", MessageBoardPage.class);
		PageParameters pageParameters = messageBoardLink.getPageParameters();
		pageParameters.set("id", game.getId());
		pageParameters.set("name", game.getName());
		return messageBoardLink;
	}
}