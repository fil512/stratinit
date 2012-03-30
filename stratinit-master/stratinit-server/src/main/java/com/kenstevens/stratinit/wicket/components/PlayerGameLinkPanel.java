package com.kenstevens.stratinit.wicket.components;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.wicket.unit.PlayerUnitsPage;

public class PlayerGameLinkPanel extends Panel {
	private static final long serialVersionUID = 4303067574741765294L;

	public PlayerGameLinkPanel(String id, SINation nation) {
		super(id);	
		BookmarkablePageLink<PlayerUnitsPage> playerUnitsLink = getPlayerUnitsLink(nation);
		playerUnitsLink.add(new Label("name", nation.name));
		add(playerUnitsLink);
	}

	private BookmarkablePageLink<PlayerUnitsPage> getPlayerUnitsLink(
			final SINation nation) {
		BookmarkablePageLink<PlayerUnitsPage> playerUnitsLink = new BookmarkablePageLink<PlayerUnitsPage>(
				"playerGameLink", PlayerUnitsPage.class);
		PageParameters pageParameters = playerUnitsLink.getPageParameters();
		pageParameters.set("gameId", nation.gameId);
		pageParameters.set("name", nation.name);
		return playerUnitsLink;
	}
}
