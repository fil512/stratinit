package com.kenstevens.stratinit.wicket.nav;

import org.apache.wicket.Component;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.wicket.base.NavProvider;

@Service
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
