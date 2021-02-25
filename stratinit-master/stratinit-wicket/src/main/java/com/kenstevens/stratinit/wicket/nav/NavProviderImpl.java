package com.kenstevens.stratinit.wicket.nav;

import com.kenstevens.stratinit.wicket.base.NavProvider;
import org.apache.wicket.Component;
import org.springframework.stereotype.Service;

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
