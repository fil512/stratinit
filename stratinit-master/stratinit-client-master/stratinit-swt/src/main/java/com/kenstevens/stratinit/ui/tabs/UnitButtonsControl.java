package com.kenstevens.stratinit.ui.tabs;

import com.google.common.eventbus.Subscribe;
import com.kenstevens.stratinit.client.api.IEventSelector;
import com.kenstevens.stratinit.client.control.selection.SelectSectorEvent;
import com.kenstevens.stratinit.client.control.selection.SelectUnitsEvent;
import com.kenstevens.stratinit.client.event.NationListArrivedEvent;
import com.kenstevens.stratinit.client.event.StratinitEventBus;
import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.client.site.action.post.ActionFactory;
import com.kenstevens.stratinit.client.util.Spring;
import com.kenstevens.stratinit.shell.TopShell;
import com.kenstevens.stratinit.shell.WidgetContainer;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.ui.adapter.*;
import com.kenstevens.stratinit.ui.image.ImageLibrary;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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
	private IEventSelector eventSelector;
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
	protected StratinitEventBus eventBus;

	private final UnitButtons unitButtons;
	private WorldSector selectedSector;

	public UnitButtonsControl(UnitButtons unitButtons) {
		this.unitButtons = unitButtons;
		setInitialImages();
		setCentreHomeEnabled(false);
	}

	@Subscribe
	public void handleNationListArrivedEvent(NationListArrivedEvent event) {
		NationView ally = db.getAlly();
		unitButtons.getCedeButton().setEnabled(ally != null);
	}

	@Subscribe
	public void handleSelectSectorEvent(SelectSectorEvent event) {
		setSelectedSector();
		noUnitsSelected();
	}

	@Subscribe
	public void handleSelectUnitsEvent(SelectUnitsEvent event) {
		setSelectedSector();
		unitsSelected();
	}
		
	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		setButtonListeners();
		eventBus.register(this);
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
		unitButtons.getCancelMoveButton().setEnabled(
				firstUnit.getUnitMove() != null);
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
				new CedeUnitsSelectionAdapter(eventSelector, spring,
						actionFactory, topShell, cedeWindow));
		unitButtons.getDisbandButton().addSelectionListener(
				new DisbandSelectionAdapter(eventSelector, spring, actionFactory,
						topShell));
		unitButtons.getCentreUnitButton().addSelectionListener(
				new CentreHomeSelectionAdapter(widgetContainer, eventSelector));
		unitButtons.getCentreHomeButton().addSelectionListener(
				new HomeSelectionAdapter(widgetContainer, db));
		unitButtons.getBuildCityButton().addSelectionListener(
				new BuildCitySelectionAdapter(eventSelector, spring,
						actionFactory, topShell));
		unitButtons.getSwitchTerrainButton().addSelectionListener(
				new SwitchTerrainSelectionAdapter(eventSelector, spring,
						actionFactory, topShell));
		unitButtons.getCancelMoveButton().addSelectionListener(
				new CancelMoveSelectionAdapter(eventSelector, spring,
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
				unitButtons.getUpdateButton()
						.setImage(imageLibrary.getUpdate());
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
