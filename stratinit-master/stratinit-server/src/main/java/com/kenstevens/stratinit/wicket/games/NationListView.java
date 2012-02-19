package com.kenstevens.stratinit.wicket.games;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;

import com.kenstevens.stratinit.dto.SINation;

final class NationListView extends ListView<SINation> {

	private static final long serialVersionUID = 1L;

	NationListView(String id,
			IModel<? extends List<? extends SINation>> model) {
		super(id, model);
	}

	@Override
	protected void populateItem(ListItem<SINation> item) {
		final SINation nation = item.getModelObject();
		item.add(new Label("name", nation.name));
		item.add(new Label("cities", "" + nation.cities));
		item.add(new Label("power", "" + nation.power));
	}
}