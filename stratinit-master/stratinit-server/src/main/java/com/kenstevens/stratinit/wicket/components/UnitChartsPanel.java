package com.kenstevens.stratinit.wicket.components;

import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.template.JavaScriptTemplate;
import org.apache.wicket.util.template.PackageTextTemplate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.type.UnitBaseType;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.wicket.security.AuthenticatedPanel;
import com.kenstevens.stratinit.wicket.unit.PlayerUnitsProvider;

@SuppressWarnings("serial")
public class UnitChartsPanel extends AuthenticatedPanel {

	private final int gameId;
	private final String username;
	private final PlayerUnitsProvider playerUnitsProvider;

	public UnitChartsPanel(String id, PlayerUnitsProvider playerUnitsProvider, int gameId, String username) {
		super(id);
		this.playerUnitsProvider = playerUnitsProvider;
		this.gameId = gameId;
		this.username = username;
	}

	public void renderHead(IHeaderResponse response) {
		String js = "";
		js += getChartJS(response, UnitBaseType.LAND);
		js += getChartJS(response, UnitBaseType.NAVY);
		js += getChartJS(response, UnitBaseType.AIR);
		js += getChartJS(response, UnitBaseType.TECH);
		response.renderString(js);
	}

	private String getChartJS(IHeaderResponse response, UnitBaseType unitBaseType) {
		PackageTextTemplate ptTemplate = new PackageTextTemplate(
				this.getClass(), "playerUnitsChart.js");
		JavaScriptTemplate jsTemplate = new JavaScriptTemplate(ptTemplate);
		Map<String, Object> parameters = Maps.newHashMap();

		parameters.put("unitTypes", getUnitTypes(unitBaseType));
		parameters.put("unitTypesBuilt", getUnitTypesBuilt(unitBaseType));
		parameters.put("unitBaseType", capitalize(unitBaseType.toString()));
		return jsTemplate.asString(parameters);
	}

	private String capitalize(String string) {
		return Strings.capitalize(string.toLowerCase());
	}

	private List<List<Object>> getUnitTypesBuilt(UnitBaseType unitBaseType) {
		return playerUnitsProvider.getFullUnitsByNation(unitBaseType, gameId, username);
	}

	private List<String> getUnitTypes(UnitBaseType unitBaseType) {
		List<String> retval = Lists.newArrayList();
		for (UnitType unitType :  UnitBase.orderedUnitTypes()) {
			if (unitBaseType != UnitBase.getUnitBase(unitType).getUnitBaseType()) {
				continue;
			}
			if (unitType == UnitType.RESEARCH) {
				continue;
			}
			retval.add(quote(capitalize(unitType.toString())));
		}
		return retval;
	}

	private String quote(String string) {
		return "'" + string + "'";
	}
}
