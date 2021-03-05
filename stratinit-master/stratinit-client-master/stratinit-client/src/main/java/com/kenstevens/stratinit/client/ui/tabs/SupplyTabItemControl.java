package com.kenstevens.stratinit.client.ui.tabs;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.client.control.TopLevelController;
import com.kenstevens.stratinit.client.control.UnitTableControl;
import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.client.util.Spring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Scope("prototype")
@Component
public class SupplyTabItemControl implements TopLevelController {
	@Autowired
	Spring spring;
	@Autowired
	Data db;

	private final SupplyTabItem supplyTabItem;

	public SupplyTabItemControl(SupplyTabItem supplyTabItem) {
		this.supplyTabItem = supplyTabItem;
	}

	public void setControllers() {
		Predicate<Unit> supplyPredicate = new Predicate<Unit>() {

			@Override
			public boolean apply(Unit unit) {
				return unit.isSupply() && unit.getNation().equals(db.getNation());
			}
			
		};
		
		Comparator<UnitView> byCoords = new Comparator<UnitView>() {

			@Override
			public int compare(UnitView a, UnitView b) {
				return a.getCoords().compareTo(b.getCoords());
			}
		};
		
		UnitTable unitTable = supplyTabItem.getUnitTable();
		spring.autowire(new UnitTableControl(unitTable.getTable(), supplyPredicate, byCoords, false, unitTable.isShowCoords()));

	}

	public void setContents() {
	}
}
