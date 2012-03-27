package com.kenstevens.stratinit.ui.tabs;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class CedeWindowControl {
	@Autowired
	private Data db;
	@Autowired
	private Spring spring;

	private final CedeWindow cedeWindow;
	private City city = null;
	private List<UnitView> units = null;

	public CedeWindowControl(CedeWindow cedeWindow, City city) {
		this.cedeWindow = cedeWindow;
		this.city = city;
	}

	public CedeWindowControl(CedeWindow cedeWindow, List<UnitView> units) {
		this.cedeWindow = cedeWindow;
		this.units = units;
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void setButtonListeners() {
		cedeWindow.getCedeButton().addSelectionListener( spring.autowire(new CedeWindowSelectionAdapter(city, units, cedeWindow)));
		

	}


	public void setContents() {
		// FIXME why this no get called?
		if (city == null) {
		    if (units.size() == 0) {
		    	return;
		    }
			Unit unit = units.get(0);
			if (units.size() == 1) {
				cedeWindow.getCedeLabel().setText("Give "+unit.toMyString()+" at "+unit.getCoords()+" to ");
			} else {
				cedeWindow.getCedeLabel().setText("Give units at "+unit.getCoords()+" to ");
			}
		} else {
			cedeWindow.getCedeLabel().setText("Give city and units at "+city.getCoords()+" to ");
		}
		final Nation ally = db.getAlly();
		if (ally == null) {
			return;
		}
		cedeWindow.getRecipientLabel().setText(ally.getName());
	}
}
