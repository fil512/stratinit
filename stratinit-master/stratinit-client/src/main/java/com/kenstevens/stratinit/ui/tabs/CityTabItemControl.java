package com.kenstevens.stratinit.ui.tabs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.control.CityTableControl;
import com.kenstevens.stratinit.control.TopLevelController;
import com.kenstevens.stratinit.util.Spring;

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
