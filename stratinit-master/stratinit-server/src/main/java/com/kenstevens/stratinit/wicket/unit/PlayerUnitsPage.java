package com.kenstevens.stratinit.wicket.unit;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.kenstevens.stratinit.wicket.BasePage;
import com.kenstevens.stratinit.wicket.game.GameArchiveListProvider;
import com.kenstevens.stratinit.wicket.game.GameListProvider;
import com.kenstevens.stratinit.wicket.game.GamePlayerTablePanel;
import com.kenstevens.stratinit.wicket.game.NationListView;

public class PlayerUnitsPage extends BasePage {
	@SpringBean
	GameArchiveListProvider gameArchiveListProvider;

	private static final long serialVersionUID = 1L;

	public PlayerUnitsPage(PageParameters pageParameters) {
		super(pageParameters);

		int gameId = pageParameters.get("gameId").toInt();
		String username = pageParameters.get("name").toString();
		add(new Label("username", username));
		add(new Label("game", "#" + gameId));
		NationListView nationView = new NationListView("nations",
				new NationListModel(gameArchiveListProvider, gameId), true);
		GamePlayerTablePanel playerTable = new GamePlayerTablePanel(
				"gamePlayerTablePanel", nationView);
		add(playerTable);
		add(new UnitChartsPanel("unitCharts", gameId,
				username));
	}

}
