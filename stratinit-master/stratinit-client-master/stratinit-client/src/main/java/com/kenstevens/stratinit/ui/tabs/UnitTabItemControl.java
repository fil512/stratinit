package com.kenstevens.stratinit.ui.tabs;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.control.TopLevelController;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class UnitTabItemControl implements TopLevelController {


	final Logger logger = Logger.getLogger(getClass());

	@Autowired
	private Spring spring;
	@Autowired Data db;

	private final UnitTabItem unitTabItem;

	private UnitButtonsControl unitButtonsControl;

	public UnitTabItemControl(UnitTabItem unitTabItem) {
		this.unitTabItem = unitTabItem;
	}

	public void setControllers() {
		spring.autowire(new UnitListTreeControl(
				unitTabItem));
		unitButtonsControl = spring.autowire(new UnitButtonsControl(unitTabItem.getUnitButtons()));
	}
	
	public UnitButtonsControl getUnitButtonsControl() {
		return unitButtonsControl;
	}
}
