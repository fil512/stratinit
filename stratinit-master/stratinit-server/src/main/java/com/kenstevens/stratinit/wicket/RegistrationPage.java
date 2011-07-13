package com.kenstevens.stratinit.wicket;

import org.apache.wicket.markup.html.IHeaderResponse;

import com.kenstevens.stratinit.wicket.admin.RegistrationForm;


public class RegistrationPage extends BasePage {

	private static final long serialVersionUID = 1L;

	public RegistrationPage() {
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
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