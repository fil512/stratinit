package com.kenstevens.stratinit.ui.tabs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.control.HistoryTextControl;
import com.kenstevens.stratinit.control.TopLevelController;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class HistoryTabItemControl implements TopLevelController {
	@Autowired
	private Spring spring;

	private final HistoryTabItem historyTabItem;

	public HistoryTabItemControl(HistoryTabItem historyTabItem) {
		this.historyTabItem = historyTabItem;
	}

	@SuppressWarnings("unused")
	public void setControllers() {
		HistoryTextControl historyTextControl = spring.autowire(new HistoryTextControl( historyTabItem.getStyledText() ));
	}

	public void setContents() {
	}
}
