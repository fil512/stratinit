package com.kenstevens.stratinit.client.ui.tabs;

import com.google.common.base.Predicate;
import com.google.common.eventbus.Subscribe;
import com.kenstevens.stratinit.client.control.TopLevelController;
import com.kenstevens.stratinit.client.control.UnitTableControl;
import com.kenstevens.stratinit.client.control.selection.SelectSectorEvent;
import com.kenstevens.stratinit.client.control.selection.SelectUnitsEvent;
import com.kenstevens.stratinit.client.event.StratinitEventBus;
import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.client.shell.ColourMap;
import com.kenstevens.stratinit.client.util.Spring;
import com.kenstevens.stratinit.type.SectorCoords;
import org.eclipse.swt.widgets.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Comparator;

// TODO split off two controls: target sector and build dropdowns
@Scope("prototype")
@Component
public class SectorTabItemControl implements TopLevelController {

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private Data db;
	@Autowired
	private Spring spring;
	@Autowired
	private SelectedCoords selectedCoords;
	@Autowired
	protected StratinitEventBus eventBus;

	private final SectorTabItem sectorTabItem;

	private WorldSector selectedSector;
	private UnitButtonsControl unitButtonsControl;

	public SectorTabItemControl(SectorTabItem sectorTabItem) {
		this.sectorTabItem = sectorTabItem;
	}

	
	
	public void setControllers() {
		Comparator<UnitView> byTypeByMoves = (a, b) -> {
			int typeComparison = a.getType().compareTo(b.getType());
			if (typeComparison != 0) {
				return typeComparison;
			} else return Integer.compare(b.getMobility(), a.getMobility());
		};

		Predicate<Unit> selectedUnitsPredicate = unit -> unit.getCoords().equals(selectedCoords.getCoords());
		UnitTable unitTable = sectorTabItem.getUnitTable();
		spring.autowire(new UnitTableControl(unitTable.getTable(), selectedUnitsPredicate, byTypeByMoves, true, unitTable.isShowCoords()));
		spring.autowire(new BuildingCombosControl(sectorTabItem
				.getBuildingCombos()));
		unitButtonsControl = spring.autowire(new UnitButtonsControl(
				sectorTabItem.getUnitButtons()));
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
		sectorTabItem.getSectorCoordsLabel().setText(
				selectedSector.getCoords().toString());
		Label sectorTypeText = sectorTabItem.getSectorType();
		sectorTypeText.setText(selectedSector.getDescription());
		if (selectedSector.getNation() == null || selectedSector.isMine()) {
			sectorTypeText.setForeground(ColourMap.BLACK);
		} else {
			sectorTypeText.setForeground(ColourMap.RED);
		}
	}

	public UnitButtonsControl getUnitButtonsControl() {
		return unitButtonsControl;
	}

	private void sectorSelected() {
		SectorCoords coords = selectedCoords.getCoords();
		if (coords == null) {
			return;
		}
		WorldSector sector = db.getWorld().getWorldSectorOrNull(
				coords);
		if (sector != null) {
			setSelectedSector(sector);
		}
	}

}
