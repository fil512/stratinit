package com.kenstevens.stratinit.wicket.games;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.kenstevens.stratinit.wicket.BasePage;

public class GamesPage extends BasePage {
	private static final long serialVersionUID = 1L;
	@SpringBean
	GameActiveListProvider gameActiveListProvider;
	@SpringBean
	GameArchiveListProvider gameArchiveListProvider;

	public GamesPage(final PageParameters parameters) {
		super(parameters);
		BookmarkablePageLink<Page> gamesPageLink = new BookmarkablePageLink<Page>(
				"seeAlsoLink", GamesPage.class);
		String mode = getPageParameters().get("mode").toString();
		if ("archive".equals(mode)) {
			add(new Label("title", "Past"));
			gamesPageLink.add(new Label("otherTitle", "Active"));
			gamesPageLink.getPageParameters().add("mode", "active");
		} else {
			add(new Label("title", "Active"));
			gamesPageLink.add(new Label("otherTitle", "Past"));
			gamesPageLink.getPageParameters().add("mode", "archive");
		}
		add(gamesPageLink);
		GameListProvider gameListProvider;
		if ("archive".equals(mode)) {
			gameListProvider = gameArchiveListProvider;
		} else {
			gameListProvider = gameActiveListProvider;
		}
		final ListView<GameTable> gameView = new GameListView("games",
				new GameListModel(gameListProvider));
		add(gameView);
	}

}
