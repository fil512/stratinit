package com.kenstevens.stratinit.wicket.unit;

import com.google.common.collect.Maps;
import com.kenstevens.stratinit.wicket.provider.UnitsBuiltProvider;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.JavaScriptTemplate;
import org.apache.wicket.util.template.PackageTextTemplate;

import java.util.Map;

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
		response.render(OnDomReadyHeaderItem.forScript(getChartJS()));
	}

	private String getChartJS() {
		PackageTextTemplate ptTemplate = new PackageTextTemplate(
				this.getClass(), "gameUnitsChart.js");
		JavaScriptTemplate jsTemplate = new JavaScriptTemplate(ptTemplate);
		Map<String, Object> parameters = Maps.newHashMap();
		parameters.put("gameId", gameId);
		parameters.put("unitLove", unitsBuiltprovider.getUnitLove(gameId));
		return jsTemplate.asString(parameters);
	}
}
