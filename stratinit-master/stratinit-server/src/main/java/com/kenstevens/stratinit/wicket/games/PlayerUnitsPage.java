package com.kenstevens.stratinit.wicket.games;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.kenstevens.stratinit.wicket.BasePage;

public class PlayerUnitsPage extends BasePage {
	@SpringBean
	PlayerUnitsProvider playerUnitsProvider;

	private static final long serialVersionUID = 1L;

	public PlayerUnitsPage(PageParameters pageParameters) {
		super(pageParameters);
		PlayerUnitsView playerUnitsView = new PlayerUnitsView("playerUnits",
				new PlayerUnitsModel(playerUnitsProvider, pageParameters.get(
						"gameId").toInt(), pageParameters.get("name")
						.toString()));
		add(playerUnitsView);
	}

}
