package com.kenstevens.stratinit.ui.tabs;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.control.TopLevelController;

@Scope("prototype")
@Component
public class SupplyTabItemControl implements TopLevelController {

	@SuppressWarnings("unused")
	private final SupplyTabItem supplyTabItem;

	public SupplyTabItemControl(SupplyTabItem futureTabItem) {
		this.supplyTabItem = futureTabItem;
	}

	public void setControllers() {
	}

	public void setContents() {
	}
}
