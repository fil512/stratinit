package com.kenstevens.stratinit.ui.tabs;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.NationView;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.site.action.ActionFactory;
import com.kenstevens.stratinit.ui.shell.StatusReporter;

@Scope("prototype")
@Component
public class CedeWindowControl {
	private final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private ActionFactory actionFactory;
	@Autowired
	private Data db;
	@Autowired
	private StatusReporter statusReporter;

	private final CedeWindow cedeWindow;
	private City city = null;
	private List<UnitView> units = null;

	public CedeWindowControl(CedeWindow cedeWindow, City city) {
		this.cedeWindow = cedeWindow;
		this.city = city;
		setButtonListeners();
	}

	public CedeWindowControl(CedeWindow cedeWindow, List<UnitView> units) {
		this.cedeWindow = cedeWindow;
		this.units = units;
		setButtonListeners();
	}

	private void setButtonListeners() {
		cedeWindow.getCedeButton().addSelectionListener(
				new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						try {
							NationView ally = db.getAlly();
							if (ally == null) {
								return;
							}
							if (city != null) {
								actionFactory.cede(city, ally);
							} else if (!units.isEmpty()) {
								actionFactory.cede(units, ally);
							} else {
								statusReporter.reportError("Nothing to cede.");
							}
							cedeWindow.close();
						} catch (Exception e1) {
							logger.error(e1.getMessage(), e1);
						}
					}

				});
	}


	public void setContents() {
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
