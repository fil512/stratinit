package com.kenstevens.stratinit.ui.tabs;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;
import com.kenstevens.stratinit.control.ETAHelper;
import com.kenstevens.stratinit.control.selection.SelectSectorEvent;
import com.kenstevens.stratinit.control.selection.SelectUnitsEvent;
import com.kenstevens.stratinit.event.CityListArrivedEvent;
import com.kenstevens.stratinit.event.StratinitEventBus;
import com.kenstevens.stratinit.main.ClientConstants;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.CityMove;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.SelectedCity;
import com.kenstevens.stratinit.model.SelectedCoords;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.site.action.ActionFactory;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.util.BuildHelper;

@Scope("prototype")
@Component
public class BuildingCombosControl {
	@Autowired
	private ActionFactory actionFactory;
	@Autowired
	private Data db;
	@Autowired
	private SelectedCity selectedCity;
	@Autowired
	private SelectedCoords selectedCoords;
	@Autowired
	protected StratinitEventBus eventBus;

	private final BuildingCombos buildingCombos;

	private WorldSector selectedSector;

	public BuildingCombosControl(BuildingCombos buildingCombos) {
		this.buildingCombos = buildingCombos;
	}

	@Subscribe
	public void handleCityListArrivedEvent(CityListArrivedEvent event) {
		setBuildCombos();
	}

	@Subscribe
	public void handleSelectSectorEvent(SelectSectorEvent event) {
		sectorSelected();
	}

	@Subscribe
	public void handleSelectUnitsEvent(SelectUnitsEvent event) {
		sectorSelected();
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		setComboListeners();
		setButtonListeners();
		eventBus.register(this);
	}

	private void setSelectedSector(WorldSector sector) {
		selectedSector = sector;
		updateSelectedSector();
	}

	private void updateSelectedSector() {
		if (selectedSector == null) {
			return;
		}
		if (selectedSector.isMine()) {
			setBuildCombos();
		} else {
			buildingCombos.setVisible(false);
		}
	}

	private void populateCombos(WorldSector citySector) {
		Combo buildingCombo = buildingCombos.getBuildingCombo();
		Combo nextBuildCombo = buildingCombos.getNextBuildCombo();
		buildingCombo.removeAll();
		nextBuildCombo.removeAll();
		nextBuildCombo.add(ClientConstants.NO_CHANGE);
		double tech = db.getNation().getTech();
		for (UnitType unitType : UnitBase.orderedUnitTypes()) {
			UnitBase unitBase = UnitBase.getUnitBase(unitType);
			if (unitBase.isNavy() && !db.getWorld().isCoastal(citySector)) {
				continue;
			}
			if (unitBase.getTech() > tech + Constants.TECH_NEXT_BUILD) {
				continue;
			}
			addToBuildCombo(nextBuildCombo, unitType);
			if (unitBase.getTech() > tech) {
				continue;
			}
			addToBuildCombo(buildingCombo, unitType);
		}
	}

	private void addToBuildCombo(Combo buildingCombo, UnitType unitType) {
		buildingCombo.add(unitType.toString());
	}

	private void setButtonListeners() {
		final Button switchOnTechButton = buildingCombos
				.getBtnSwitchOnTechChange();
		switchOnTechButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				City city = selectedCity.getCity();
				if (city == null) {
					return;
				}
				city.setSwitchOnTechChange(switchOnTechButton.getSelection());
				actionFactory.switchOnTechChange(city);
			}
		});
	}

	private void setBuildCombos() {
		if (selectedSector == null) {
			return;
		}
		buildingCombos.setVisible(true);
		Combo buildingCombo = buildingCombos.getBuildingCombo();
		Combo nextBuildCombo = buildingCombos.getNextBuildCombo();
		Label buildETA = buildingCombos.getBuildETA();
		Label percentDone = buildingCombos.getPercentDoneLabel();

		City city = db.getCity(selectedSector);
		if (city == null) {
			buildingCombos.setVisible(false);
		} else {
			UnitType unitType = city.getBuild();
			String unitTypeString = unitType.toString();
			populateCombos(selectedSector);
			if (buildingCombo.indexOf(unitTypeString) == -1) {
				unitTypeString = "(" + unitTypeString.toLowerCase() + ")";
			}
			buildingCombo.select(buildingCombo.indexOf(unitTypeString));
			ETAHelper etaHelper = new ETAHelper(db);
			if (atLimit()) {
				buildETA.setText("AT LIMIT");
			} else {
				buildETA.setText(etaHelper.getETA(city));
			}
			percentDone.setText(etaHelper.getPercentDone(city));
			unitType = city.getNextBuild();
			if (unitType != null) {
				nextBuildCombo.select(nextBuildCombo.indexOf(unitType
						.toString()));
			} else {
				// (no change)
				nextBuildCombo.select(0);
			}
			buildingCombos.setVisible(true);
			Button switchTechButton = buildingCombos.getBtnSwitchOnTechChange();
			switchTechButton.setSelection(city.isSwitchOnTechChange());
			Label waypointLabel = buildingCombos.getWaypoint();
			CityMove cityMove = city.getCityMove();
			if (cityMove == null) {
				waypointLabel.setText("");
			} else {
				waypointLabel.setText(cityMove.getCoords().toString());
			}
		}
	}

	private boolean atLimit() {
		int cityCount = db.getCityList().size();
		int power = db.getNation().getPower();
		return BuildHelper.powerLimitReached(cityCount, power).getValue();
	}

	private void setComboListeners() {
		final Combo buildingCombo = buildingCombos.getBuildingCombo();
		buildingCombo.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				String choice = buildingCombo.getText();
				if (choice.startsWith("(")) {
					choice = choice.substring(1, choice.length() - 1)
							.toUpperCase();
				}
				City city = selectedCity.getCity();
				if (city == null) {
					return;
				}
				actionFactory.buildUnit(city, choice);
			}
		});
		final Combo nextBuildCombo = buildingCombos.getNextBuildCombo();
		nextBuildCombo.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				String choice = nextBuildCombo.getText();
				if (choice.startsWith("(")) {
					choice = choice.substring(1, choice.length() - 2)
							.toUpperCase();
				}
				City city = selectedCity.getCity();
				if (city == null) {
					return;
				}
				if (choice.equals(ClientConstants.NO_CHANGE)) {
					choice = "";
				}
				actionFactory.nextBuildUnit(city, choice);
			}
		});
	}

	private void sectorSelected() {
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
