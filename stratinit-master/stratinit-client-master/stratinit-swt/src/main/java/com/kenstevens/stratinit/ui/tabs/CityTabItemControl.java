package com.kenstevens.stratinit.ui.tabs;

import com.kenstevens.stratinit.client.control.CityTableControl;
import com.kenstevens.stratinit.client.control.TopLevelController;
import com.kenstevens.stratinit.client.util.Spring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class CityTabItemControl implements TopLevelController {
	@Autowired
	private Spring spring;

	private final CityTabItem cityTabItem;


	public CityTabItemControl(CityTabItem cityTabItem) {
		this.cityTabItem = cityTabItem;
	}

	public void setControllers() {
		spring.autowire(new CityTableControl( cityTabItem.getCityTable() ));
		spring.autowire(new BuildingCombosControl(cityTabItem.getBuildingCombos()));
		spring.autowire(new CityButtonsControl(cityTabItem.getCityButtons()));
	}

	public void setContents() {
	}

}
