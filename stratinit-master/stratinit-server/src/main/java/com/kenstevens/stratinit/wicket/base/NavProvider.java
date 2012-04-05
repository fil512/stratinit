package com.kenstevens.stratinit.wicket.base;

import org.apache.wicket.Component;

public interface NavProvider {

	Component getTopNavPanel();

	Component getLeftNavPanel();

	Component getAnalyticsPanel();

}
