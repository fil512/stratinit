package com.kenstevens.stratinit.site.action.get;

import com.kenstevens.stratinit.audio.WavPlayer;
import com.kenstevens.stratinit.control.selection.MapCentre;
import com.kenstevens.stratinit.control.selection.SelectEvent;
import com.kenstevens.stratinit.control.selection.Selection.Source;
import com.kenstevens.stratinit.event.CityListReplacementArrivedEvent;
import com.kenstevens.stratinit.event.UnitListReplacementArrivedEvent;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.UnitList;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.site.GetAction;
import com.kenstevens.stratinit.site.command.DescriptionCommand;
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