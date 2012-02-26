package com.kenstevens.stratinit.wicket.components;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.kenstevens.stratinit.wicket.games.TeamRankPage;

public class PlayerTeamsPanel extends Panel {

	public static final String PLAYER_NAME = "playerName";
	private static final long serialVersionUID = 6178816630721977685L;

	public PlayerTeamsPanel(String id, String playerName) {
		super(id);
		PageParameters parameters = new PageParameters();
		parameters.add(PLAYER_NAME, playerName);
		BookmarkablePageLink<Page> playerTeamsLink = new BookmarkablePageLink<Page>(
				"playerTeamsLink", TeamRankPage.class, parameters);
		add(playerTeamsLink);
		playerTeamsLink.add(new Label(PLAYER_NAME, playerName));
	}
}
