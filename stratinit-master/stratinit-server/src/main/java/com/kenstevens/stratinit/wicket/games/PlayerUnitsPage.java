package com.kenstevens.stratinit.wicket.games;

import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.JavaScriptTemplate;
import org.apache.wicket.util.template.PackageTextTemplate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kenstevens.stratinit.type.UnitType;
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

	public void renderHead(IHeaderResponse response) {
		PackageTextTemplate ptTemplate = new PackageTextTemplate(
				this.getClass(), "playerUnitsChart.js");
		JavaScriptTemplate jsTemplate = new JavaScriptTemplate(ptTemplate);
		Map<String, Object> parameters = Maps.newHashMap();

		parameters.put("unitTypes", getUnitTypes());
		parameters.put("unitTypesBuilt", getUnitTypesBuilt());
		response.renderString(jsTemplate.asString(parameters));
	}

	private List<List<Object>> getUnitTypesBuilt() {
		int gameId = getPageParameters().get("gameId").toInt();
		String name = getPageParameters().get("name").toString();
		// FIXME final glue goes here.  This is the wrong output.  Change playerUnitsProvider (and BuildAuditsAggregator)
		// to return the actual array we need here which looks like [['1', 0, 2, 1, ...], ['2', 3, 1, ...]] where
		// the numbers are the amount of each unit type built each day
		List<PlayerUnitCount> playerUnits = playerUnitsProvider.getUnitsByNation(gameId, name);
		return null;
	}

	private List<String> getUnitTypes() {
		List<String> retval = Lists.newArrayList();
		for (UnitType unitType : UnitType.values()) {
			retval.add(quote(unitType.toString().toLowerCase()));
		}
		return retval;
	}

	private String quote(String string) {
		return "'" + string + "'";
	}

}
