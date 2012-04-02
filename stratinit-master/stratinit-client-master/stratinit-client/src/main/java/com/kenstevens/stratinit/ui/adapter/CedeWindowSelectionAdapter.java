package com.kenstevens.stratinit.ui.adapter;

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
import com.kenstevens.stratinit.model.NationView;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.site.action.ActionFactory;
import com.kenstevens.stratinit.ui.shell.StatusReporter;

@Scope("prototype")
@Component
public class CedeWindowSelectionAdapter extends SelectionAdapter {
	@Autowired
	private ActionFactory actionFactory;
	@Autowired
	private Data db;
	@Autowired
	private StatusReporter statusReporter;
	private final Log logger = LogFactory.getLog(getClass());
	private final City city;
	private final List<UnitView> units;

	private final CedeWindow cedeWindow;

	public CedeWindowSelectionAdapter(
			City city, List<UnitView> units, CedeWindow cedeWindow) {
		this.city = city;
		this.units = units;
		this.cedeWindow = cedeWindow;
	}

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

}
