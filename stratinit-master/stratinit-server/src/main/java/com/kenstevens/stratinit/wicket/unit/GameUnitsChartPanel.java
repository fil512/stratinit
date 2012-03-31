package com.kenstevens.stratinit.wicket.unit;

import java.util.Map;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.JavaScriptTemplate;
import org.apache.wicket.util.template.PackageTextTemplate;

import com.google.common.collect.Maps;

@SuppressWarnings("serial")
public class GameUnitsChartPanel extends Panel {
	@SpringBean
	UnitsBuiltProvider unitsBuiltprovider;
	private final int gameId;

	public GameUnitsChartPanel(String id, int gameId) {
		super(id);
		this.gameId = gameId;
		setMarkupId(gameId+"_chart_div");
	}

	public void renderHead(IHeaderResponse response) {
		response.renderString(getChartJS(response));
	}

	private String getChartJS(IHeaderResponse response) {
		PackageTextTemplate ptTemplate = new PackageTextTemplate(
				this.getClass(), "gameUnitsChart.js");
		JavaScriptTemplate jsTemplate = new JavaScriptTemplate(ptTemplate);
		Map<String, Object> parameters = Maps.newHashMap();
		parameters.put("gameId", gameId);
		parameters.put("unitLove", unitsBuiltprovider.getUnitLove(gameId));
		return jsTemplate.asString(parameters);
	}
}
