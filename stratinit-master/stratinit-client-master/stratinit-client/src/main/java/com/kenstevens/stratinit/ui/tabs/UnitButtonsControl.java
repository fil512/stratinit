package com.kenstevens.stratinit.ui.tabs;

import javax.annotation.PostConstruct;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gwt.event.shared.HandlerManager;
import com.kenstevens.stratinit.event.NationListArrivedEvent;
import com.kenstevens.stratinit.event.NationListArrivedEventHandler;
import com.kenstevens.stratinit.event.SelectSectorEvent;
import com.kenstevens.stratinit.event.SelectSectorEventHandler;
import com.kenstevens.stratinit.event.SelectUnitsEvent;
import com.kenstevens.stratinit.event.SelectUnitsEventHandler;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.NationView;
import com.kenstevens.stratinit.model.SelectedCoords;
import com.kenstevens.stratinit.model.SelectedUnits;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.site.action.ActionFactory;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.ui.adapter.BuildCitySelectionAdapter;
import com.kenstevens.stratinit.ui.adapter.CancelMoveSelectionAdapter;
import com.kenstevens.stratinit.ui.adapter.CedeUnitsSelectionAdapter;
import com.kenstevens.stratinit.ui.adapter.CentreHomeSelectionAdapter;
import com.kenstevens.stratinit.ui.adapter.DisbandSelectionAdapter;
import com.kenstevens.stratinit.ui.adapter.SwitchTerrainSelectionAdapter;
import com.kenstevens.stratinit.ui.adapter.UpdateUnitsSelectionAdapter;
import com.kenstevens.stratinit.ui.image.ImageLibrary;
import com.kenstevens.stratinit.ui.selection.SelectEvent;
import com.kenstevens.stratinit.ui.selection.Selection.Source;
import com.kenstevens.stratinit.ui.shell.TopShell;
import com.kenstevens.stratinit.ui.shell.WidgetContainer;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class UnitButtonsControl {
	@Autowired
	private ActionFactory actionFactory;
	@Autowired
	private Data db;
	@Autowired
	private SelectedCoords selectedCoords;
	@Autowired
	private SelectEvent selectEvent;
	@Autowired
	private SelectedUnits selectedUnits;
	@Autowired
	private CedeWindow cedeWindow;
	@Autowired
	private TopShell topShell;
	@Autowired
	private WidgetContainer widgetContainer;
	@Autowired
	private ImageLibrary imageLibrary;
	@Autowired
	private Spring spring;
	@Autowired
	private HandlerManager handlerManager;

	private final UnitButtons unitButtons;
	private WorldSector selectedSector;

	public UnitButtonsControl(UnitButtons unitButtons) {
		this.unitButtons = unitButtons;
		setInitialImages();
		setCentreHomeEnabled(false);
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		setButtonListeners();
		handlerManager.addHandler(NationListArrivedEvent.TYPE,
				new NationListArrivedEventHandler() {
					@Override
					public void dataArrived() {
						NationView ally = db.getAlly();
						if (ally == null) {
							unitButtons.getCedeButton().setEnabled(false);
						} else {
							unitButtons.getCedeButton().setEnabled(true);
						}
					}
				});
		handlerManager.addHandler(SelectSectorEvent.TYPE,
				new SelectSectorEventHandler() {

					@Override
					public void selectSector(Source source) {
						setSelectedSector();
						noUnitsSelected();
					}
				});
		handlerManager.addHandler(SelectUnitsEvent.TYPE,
				new SelectUnitsEventHandler() {

					@Override
					public void selectUnits(Source source) {
						setSelectedSector();
						unitsSelected();
					}
				});
	}

	private void noUnitsSelected() {
		unitButtons.getDisbandButton().setEnabled(false);
		unitButtons.getCentreUnitButton().setEnabled(false);
		unitButtons.getCancelMoveButton().setEnabled(false);
		unitButtons.setIsEngineer(false);
	}

	private void unitsSelected() {
		if (selectedUnits.isEmpty()) {
			return;
		}
		Unit firstUnit = selectedUnits.iterator().next();
		unitButtons.getDisbandButton().setEnabled(true);
		unitButtons.getCentreUnitButton().setEnabled(true);
		unitButtons.setIsEngineer(firstUnit.isEngineer());
		unitButtons.getCancelMoveButton().setEnabled(firstUnit.getUnitMove() != null);
		if (selectedSector != null) {
			Button switchTerrainButton = unitButtons.getSwitchTerrainButton();
			if (selectedSector.isLand()) {
				switchTerrainButton.setImage(imageLibrary.getDig());
				switchTerrainButton.setToolTipText("Change land to water");
			} else if (selectedSector.isWater()) {
				switchTerrainButton.setImage(imageLibrary.getFill());
				switchTerrainButton.setToolTipText("Change water to land");
			}
		}
	}

	private void setSelectedSector(WorldSector sector) {
		selectedSector = sector;
	}

	private void setButtonListeners() {
		unitButtons.getCedeButton().addSelectionListener(
				new CedeUnitsSelectionAdapter(selectEvent, spring, actionFactory,
						topShell, cedeWindow));
		unitButtons.getDisbandButton().addSelectionListener(
				new DisbandSelectionAdapter(selectEvent, spring, actionFactory,
						topShell));
		unitButtons.getCentreUnitButton().addSelectionListener(
				new CentreHomeSelectionAdapter(widgetContainer, selectEvent));
		unitButtons.getCentreHomeButton().addSelectionListener(
				new HomeSelectionAdapter(widgetContainer, db));
		unitButtons.getBuildCityButton().addSelectionListener(
				new BuildCitySelectionAdapter(selectEvent, spring,
						actionFactory, topShell));
		unitButtons.getSwitchTerrainButton().addSelectionListener(
				new SwitchTerrainSelectionAdapter(selectEvent, spring,
						actionFactory, topShell));
		unitButtons.getCancelMoveButton().addSelectionListener(
				new CancelMoveSelectionAdapter(selectEvent, spring,
						actionFactory, topShell));
		unitButtons.getUpdateButton().addSelectionListener(
				new UpdateUnitsSelectionAdapter(actionFactory));
	}

	private final void setInitialImages() {
		Display display = Display.getDefault();
		if (display.isDisposed())
			return;
		display.asyncExec(new Runnable() {
			public void run() {

				unitButtons.getCentreUnitButton().setImage(
						imageLibrary.getCentreUnit());
				unitButtons.getCentreHomeButton().setImage(
						imageLibrary.getCentreHome());
				unitButtons.getCedeButton().setImage(imageLibrary.getCede());
				unitButtons.getDisbandButton().setImage(
						imageLibrary.getDisband());
				unitButtons.getBuildCityButton().setImage(
						imageLibrary.getBuildCity());
				unitButtons.getCancelMoveButton().setImage(
						imageLibrary.getCancelMove());
				unitButtons.getUpdateButton().setImage(
						imageLibrary.getUpdate());
			}
		});
	}

	public final void setCentreHomeEnabled(boolean enabled) {
		unitButtons.getCentreHomeButton().setEnabled(enabled);
	}

	private void setSelectedSector() {
		SectorCoords coords = selectedCoords.getCoords();
		if (coords == null) {
			return;
		}
		WorldSector sector = db.getWorld().getWorldSector(coords);
		if (sector != null) {
			setSelectedSector(sector);
		}
	}
}
