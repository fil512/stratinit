package com.kenstevens.stratinit.ui.tabs;


import com.kenstevens.stratinit.client.control.TopLevelController;
import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.util.Spring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class UnitTabItemControl implements TopLevelController {


	final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private Spring spring;
	@Autowired
	Data db;

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
