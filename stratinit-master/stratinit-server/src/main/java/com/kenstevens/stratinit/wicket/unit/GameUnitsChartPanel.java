package com.kenstevens.stratinit.wicket.unit;

import java.util.Map;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.template.JavaScriptTemplate;
import org.apache.wicket.util.template.PackageTextTemplate;

import com.google.common.collect.Maps;

@SuppressWarnings("serial")
public class GameUnitsChartPanel extends Panel {

	private final int gameId;

	public GameUnitsChartPanel(String id, int gameId) {
		super(id);
		this.gameId = gameId;
		setMarkupId(gameId+"_chart_div");
		//fixme how do i set the div id?
	}

	public void renderHead(IHeaderResponse response) {
		response.renderString(getChartJS(response));
	}

	private String getChartJS(IHeaderResponse response) {
		PackageTextTemplate ptTemplate = new PackageTextTemplate(
				this.getClass(), "gameUnitsChart.js");
		JavaScriptTemplate jsTemplate = new JavaScriptTemplate(ptTemplate);
		Map<String, Object> parameters = Maps.newHashMap();
// FIXME
		parameters.put("gameId", gameId);
//		parameters.put("unitTypesBuilt", getUnitTypesBuilt(unitBaseType));
//		parameters.put("unitBaseType", capitalize(unitBaseType.toString()));
		return jsTemplate.asString(parameters);
	}
}
