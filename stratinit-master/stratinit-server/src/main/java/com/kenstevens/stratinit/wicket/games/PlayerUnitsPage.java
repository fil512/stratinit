package com.kenstevens.stratinit.wicket.games;

import java.util.Map;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.JavaScriptTemplate;
import org.apache.wicket.util.template.PackageTextTemplate;

import com.google.common.collect.Maps;
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
		PackageTextTemplate ptTemplate = new PackageTextTemplate(this.getClass(), "playerUnitsChart.tpl");
		JavaScriptTemplate jsTemplate = new JavaScriptTemplate(ptTemplate);
		Map< String , Object > parameters = Maps.newHashMap();
		parameters.put("title","Zoo Company Performance");
		response.renderString(jsTemplate.asString(parameters));
//		add(TextTemplateHeaderContributor.forJavaScript(jsTemplate, new  Model((Serializable) parameters)));
//		Label getOptions = new Label(
//				"getOptionsScript",
//				"function getOptions() { return { title: 'Company Performance',	hAxis: {title: 'Year', titleTextStyle: {color: 'red'}}}}");
//		getOptions.setEscapeModelStrings(false);
//		add(getOptions);
	}

}
