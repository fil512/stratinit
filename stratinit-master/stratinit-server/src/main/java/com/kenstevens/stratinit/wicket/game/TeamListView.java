package com.kenstevens.stratinit.wicket.game;

import com.kenstevens.stratinit.dto.SITeam;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.List;

public final class TeamListView extends ListView<SITeam> {

	private static final long serialVersionUID = 1L;

	public TeamListView(String id, List<SITeam> model) {
		super(id, model);
	}

	@Override
	protected void populateItem(ListItem<SITeam> item) {
		final SITeam team = item.getModelObject();
		item.add(new Label("nation1", team.nation1));
		item.add(new Label("nation2", team.nation2));
		item.add(new Label("score", "" + team.score));
	}
}