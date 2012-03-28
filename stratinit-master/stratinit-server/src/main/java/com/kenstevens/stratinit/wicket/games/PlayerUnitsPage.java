package com.kenstevens.stratinit.wicket.games;

import java.awt.Dimension;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.googlecharts.AbstractChartData;
import org.wicketstuff.googlecharts.Chart;
import org.wicketstuff.googlecharts.ChartDataEncoding;
import org.wicketstuff.googlecharts.ChartProvider;
import org.wicketstuff.googlecharts.ChartType;

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
		Chart chart = getChart();
		add(chart);
	}

	private Chart getChart() {
		@SuppressWarnings("serial")
		AbstractChartData data = new AbstractChartData(ChartDataEncoding.TEXT,
				100) {

			public double[][] getData() {
				return new double[][] { { 100, 80, 60, 30, 30, 30, 10 } };
			}
		};

		ChartProvider provider = new ChartProvider(new Dimension(200, 100),
				ChartType.VENN, data);

		Chart chart = new Chart("venn", provider);
		return chart;
	}

}
