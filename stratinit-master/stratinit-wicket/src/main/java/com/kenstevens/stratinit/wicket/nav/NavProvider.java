package com.kenstevens.stratinit.wicket.nav;

import org.apache.wicket.Component;
import org.springframework.stereotype.Service;

@Service
public class NavProvider {
	public Component getTopNavPanel() {
		return new TopNavPanel("topNavPanel");
	}

	public Component getLeftNavPanel() {
		return new LeftNavPanel("leftNavPanel");
	}

	public Component getAnalyticsPanel() {
		return new GoogleAnalyticsPanel("googleAnalyticsPanel");
	}
}
