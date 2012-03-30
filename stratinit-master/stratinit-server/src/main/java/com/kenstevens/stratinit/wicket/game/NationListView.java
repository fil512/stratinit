package com.kenstevens.stratinit.wicket.game;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;

import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.wicket.components.PlayerGameLinkPanel;

final class NationListView extends ListView<SINation> {

	private static final long serialVersionUID = 1L;
	private final boolean hasEnded;

	NationListView(String id, IModel<? extends List<? extends SINation>> model, boolean hasEnded) {
		super(id, model);
		this.hasEnded = hasEnded;
	}

	@Override
	protected void populateItem(ListItem<SINation> item) {
		final SINation nation = item.getModelObject();
		item.add(new Label("cities", "" + nation.cities));
		item.add(new Label("power", "" + nation.power));
		if (hasEnded) {
			item.add(new PlayerGameLinkPanel("playerGameLink", nation));
		} else {
			item.add(new Label("playerGameLink", nation.name));
		}
	}

}