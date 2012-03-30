package com.kenstevens.stratinit.wicket.unit;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.kenstevens.stratinit.wicket.BasePage;
import com.kenstevens.stratinit.wicket.components.UnitChartsPanel;

public class PlayerUnitsPage extends BasePage {
	@SpringBean
	PlayerUnitsProvider playerUnitsProvider;

	private static final long serialVersionUID = 1L;

	public PlayerUnitsPage(PageParameters pageParameters) {
		super(pageParameters);
		
		int gameId = pageParameters.get(
				"gameId").toInt();
		String username = pageParameters.get("name")
				.toString();
		add(new Label("username", username));
		add(new Label("game", "#"+gameId));
		add(new UnitChartsPanel("unitCharts", playerUnitsProvider, gameId, username));
	}



}
