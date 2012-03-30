package com.kenstevens.stratinit.wicket.components;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;

import com.kenstevens.stratinit.model.Rankable;

public class RankedPanel extends Panel {
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
