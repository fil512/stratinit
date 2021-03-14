package com.kenstevens.stratinit.client.site.action.get;

import com.kenstevens.stratinit.client.audio.WavPlayer;
import com.kenstevens.stratinit.client.control.selection.MapCentre;
import com.kenstevens.stratinit.client.control.selection.SelectEvent;
import com.kenstevens.stratinit.client.control.selection.Selection.Source;
import com.kenstevens.stratinit.client.event.CityListReplacementArrivedEvent;
import com.kenstevens.stratinit.client.event.UnitListReplacementArrivedEvent;
import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.model.UnitList;
import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.client.site.GetAction;
import com.kenstevens.stratinit.client.site.command.get.DescriptionCommand;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class StartupAction extends GetAction<DescriptionCommand> {
	@Autowired
	private WavPlayer wavPlayer;
	@Autowired
	private MapCentre mapCentre;
	@Autowired
	private Data db;
	@Autowired
	private SelectEvent selectEvent;

	protected StartupAction() {
		super(new DescriptionCommand("Start up"));
	}

	@Override
	public void postRequest() {
		arrivedDataEventAccumulator.addEvent(new CityListReplacementArrivedEvent());
		arrivedDataEventAccumulator.addEvent(new UnitListReplacementArrivedEvent());
		centreMapOnUnit();
		wavPlayer.playFinishedLoading();
	}

	private void centreMapOnUnit() {
		UnitList unitList = db.getUnitList();
		if (unitList.isEmpty()) {
			return;
		}
		UnitView unit = unitList.getUnits().get(0);
		selectUnit(unit);
		mapCentre.init();
	}

	private void selectUnit(final UnitView unit) {
		Display display = Display.getDefault();

		if (display.isDisposed())
			return;
		display.asyncExec(new Runnable() {
			public void run() {
				selectEvent.selectUnit(unit, Source.UNIT_TAB);
			}
		});
	}
}