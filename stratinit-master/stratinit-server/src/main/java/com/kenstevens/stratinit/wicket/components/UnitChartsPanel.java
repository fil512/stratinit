package com.kenstevens.stratinit.wicket.components;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.template.JavaScriptTemplate;
import org.apache.wicket.util.template.PackageTextTemplate;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.type.UnitBaseType;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.wicket.unit.DayUnitsListView;
import com.kenstevens.stratinit.wicket.unit.DayUnitsModel;
import com.kenstevens.stratinit.wicket.unit.PlayerUnitsProvider;

@SuppressWarnings("serial")
public class UnitChartsPanel extends Panel {

	private final int gameId;
	private final String username;
	private final PlayerUnitsProvider playerUnitsProvider;

	public UnitChartsPanel(String id, PlayerUnitsProvider playerUnitsProvider, int gameId, String username) {
		super(id);
		this.playerUnitsProvider = playerUnitsProvider;
		this.gameId = gameId;
		this.username = username;
		DayUnitsListView landUnitsListView = new DayUnitsListView("unitRow",
				new DayUnitsModel(playerUnitsProvider, gameId, username, UnitBaseType.LAND));
		DayUnitsListView navyUnitsListView = new DayUnitsListView("unitRow",
				new DayUnitsModel(playerUnitsProvider, gameId, username, UnitBaseType.NAVY));
		DayUnitsListView airUnitsListView = new DayUnitsListView("unitRow",
				new DayUnitsModel(playerUnitsProvider, gameId, username, UnitBaseType.AIR));
		DayUnitsListView techUnitsListView = new DayUnitsListView("unitRow",
				new DayUnitsModel(playerUnitsProvider, gameId, username, UnitBaseType.TECH));
		add(new UnitTablePanel("landTable", landUnitsListView));
		add(new UnitTablePanel("navyTable", navyUnitsListView));
		add(new UnitTablePanel("airTable", airUnitsListView));
		add(new UnitTablePanel("techTable", techUnitsListView));
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

	private Collection<String> getUnitTypes(UnitBaseType unitBaseType) {
		return Collections2.transform(UnitBase.orderedUnitTypes(unitBaseType), new Function<UnitType, String>() {
			@Override
			public String apply(UnitType unitType) {
				return quote(capitalize(unitType.toString()));
			}
		});
	}

	private String quote(String string) {
		return "'" + string + "'";
	}
}
