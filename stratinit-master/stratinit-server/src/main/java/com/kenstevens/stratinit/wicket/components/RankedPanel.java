package com.kenstevens.stratinit.wicket.components;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;

import com.kenstevens.stratinit.model.Rankable;
import com.kenstevens.stratinit.wicket.security.AuthenticatedPanel;

public class RankedPanel extends AuthenticatedPanel {
	private static final long serialVersionUID = 4303067574741765294L;
	public static final String TEAM = "Team";
	public static final String PLAYER = "Player";

	public RankedPanel(String id, String type,
			PageableListView<? extends Rankable> rankableView) {
		super(id);

		add(rankableView);
		add(new Label("type", type));
		add(new PagingNavigator("navigator", rankableView));

	}
}
