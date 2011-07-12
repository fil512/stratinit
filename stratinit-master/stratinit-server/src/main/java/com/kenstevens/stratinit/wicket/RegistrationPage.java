package com.kenstevens.stratinit.wicket;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

import com.kenstevens.stratinit.wicket.admin.RegistrationForm;


public class RegistrationPage extends BasePage {

	private static final ResourceReference registrationCSS = new PackageResourceReference(RegistrationPage.class, "RegistrationPage.css");


	public RegistrationPage() {
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.renderCSSReference(registrationCSS);
	}
	
	@Override
	protected void onBeforeRender() {
		if (!hasBeenRendered()) {
			init();
		}
		super.onBeforeRender();
	}

	final void init() {
		add(new RegistrationForm("registrationForm"));
	}

	
}