package com.kenstevens.stratinit.wicket.game;

import org.apache.wicket.markup.html.panel.Panel;

public class GamePlayerTablePanel extends Panel {

	private static final long serialVersionUID = 1L;

	public GamePlayerTablePanel(String id, NationListView nationView) {
		super(id);
		add(nationView);
	}
}
