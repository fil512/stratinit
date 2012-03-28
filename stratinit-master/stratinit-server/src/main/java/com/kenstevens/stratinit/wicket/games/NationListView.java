package com.kenstevens.stratinit.wicket.games;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.kenstevens.stratinit.dto.SINation;

final class NationListView extends ListView<SINation> {

	private static final long serialVersionUID = 1L;

	NationListView(String id, IModel<? extends List<? extends SINation>> model) {
		super(id, model);
	}

	@Override
	protected void populateItem(ListItem<SINation> item) {
		final SINation nation = item.getModelObject();
		item.add(new Label("cities", "" + nation.cities));
		item.add(new Label("power", "" + nation.power));
		BookmarkablePageLink<PlayerUnitsPage> playerUnitsLink = getPlayerUnitsLink(nation);
		playerUnitsLink.add(new Label("name", nation.name));
		item.add(playerUnitsLink);
	}

	private BookmarkablePageLink<PlayerUnitsPage> getPlayerUnitsLink(
			final SINation nation) {
		BookmarkablePageLink<PlayerUnitsPage> playerUnitsLink = new BookmarkablePageLink<PlayerUnitsPage>(
				"playerUnitsLink", PlayerUnitsPage.class);
		PageParameters pageParameters = playerUnitsLink.getPageParameters();
		pageParameters.set("gameId", nation.gameId);
		pageParameters.set("name", nation.name);
		return playerUnitsLink;
	}
}