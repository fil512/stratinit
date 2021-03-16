package com.kenstevens.stratinit.ui.adapter;

import com.kenstevens.stratinit.client.api.IStatusReporter;
import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.model.NationView;
import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.client.site.action.post.ActionFactory;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class CedeWindowSelectionAdapter extends SelectionAdapter {
	@Autowired
	private ActionFactory actionFactory;
	@Autowired
	private Data db;
	@Autowired
	private IStatusReporter statusReporter;
	private final Logger logger = LoggerFactory.getLogger(getClass());
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
