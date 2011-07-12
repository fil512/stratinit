package com.kenstevens.stratinit.wicket;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.wicket.components.SignOutPanel;

public class BasePage extends WebPage {
	private static final long serialVersionUID = 1L;
	
	private static final ResourceReference baseCSS = new PackageResourceReference(ErrorPage.class, "BasePage.css");

	public BasePage() {
		this(null);
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.renderCSSReference(baseCSS);
	}
	
    public BasePage(final PageParameters parameters) {
    	add(new SignOutPanel("signOutPanel"));
		add(new Label("version", Constants.SERVER_VERSION));
		add(new FeedbackPanel("feedback"));
    }
}
