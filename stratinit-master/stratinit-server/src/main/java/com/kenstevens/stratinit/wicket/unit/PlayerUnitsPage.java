package com.kenstevens.stratinit.wicket.unit;

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
		add(new UnitChartsPanel("unitCharts", playerUnitsProvider, pageParameters.get(
				"gameId").toInt(), pageParameters.get("name")
				.toString()));
	}



}
