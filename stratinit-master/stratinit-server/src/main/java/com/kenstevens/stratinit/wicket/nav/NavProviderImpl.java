package com.kenstevens.stratinit.wicket.nav;

import org.apache.wicket.Component;

import com.kenstevens.stratinit.wicket.base.NavProvider;

public class NavProviderImpl implements NavProvider {

	@Override
	public Component getTopNavPanel() {
		return new TopNavPanel("topNavPanel");
	}

	@Override
	public Component getLeftNavPanel() {
		return new LeftNavPanel("leftNavPanel");
	}

	@Override
	public Component getAnalyticsPanel() {
		return new GoogleAnalyticsPanel("googleAnalyticsPanel");
	}

}
