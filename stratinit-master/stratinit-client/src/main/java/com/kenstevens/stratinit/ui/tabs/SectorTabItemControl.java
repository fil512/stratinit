package com.kenstevens.stratinit.ui.tabs;

import java.util.Comparator;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.base.Predicate;
import com.google.gwt.event.shared.HandlerManager;
import com.kenstevens.stratinit.control.TopLevelController;
import com.kenstevens.stratinit.control.UnitTableControl;
import com.kenstevens.stratinit.event.SelectSectorEvent;
import com.kenstevens.stratinit.event.SelectSectorEventHandler;
import com.kenstevens.stratinit.event.SelectUnitsEvent;
import com.kenstevens.stratinit.event.SelectUnitsEventHandler;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.SelectedCoords;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.ui.image.ColourMap;
import com.kenstevens.stratinit.ui.selection.Selection.Source;
import com.kenstevens.stratinit.util.Spring;

// TODO split off two controls: target sector and build dropdowns
@Scope("prototype")
@Component
public class SectorTabItemControl implements TopLevelController {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(getClass());
	@Autowired
	private Data db;
	@Autowired
	private Spring spring;
	@Autowired
	private HandlerManager handlerManager;
	@Autowired
	private SelectedCoords selectedCoords;

	private final SectorTabItem sectorTabItem;

	private WorldSector selectedSector;
	private UnitButtonsControl unitButtonsControl;

	public SectorTabItemControl(SectorTabItem sectorTabItem) {
		this.sectorTabItem = sectorTabItem;
	}

	
	
	public void setControllers() {
		Comparator<UnitView> byTypeByMoves = new Comparator<UnitView>() {

			@Override
			public int compare(UnitView a, UnitView b) {
				int typeComparison = a.getType().compareTo(b.getType());
				if (typeComparison != 0) {
					return typeComparison;
				} else if (a.getMobility() < b.getMobility()) {
					return 1;
				} else if (a.getMobility() > b.getMobility()) {
					return -1;
				} else {
					return 0;
				}
			}
		};
		
		Predicate<Unit> selectedUnitsPredicate = new Predicate<Unit>() {
			@Override
			public boolean apply(Unit unit) {
				return unit.getCoords().equals(selectedCoords.getCoords());
			}
			
		};
		spring.autowire(new UnitTableControl(sectorTabItem.getUnitTable(), selectedUnitsPredicate, byTypeByMoves, true));
		spring.autowire(new BuildingCombosControl(sectorTabItem
				.getBuildingCombos()));
		unitButtonsControl = spring.autowire(new UnitButtonsControl(
				sectorTabItem.getUnitButtons()));
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		handlerManager.addHandler(SelectSectorEvent.TYPE,
				new SelectSectorEventHandler() {

					@Override
					public void selectSector(Source source) {
						sectorSelected();
					}
				});
		handlerManager.addHandler(SelectUnitsEvent.TYPE,
				new SelectUnitsEventHandler() {

					@Override
					public void selectUnits(Source source) {
						sectorSelected();
					}
				});
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
		WorldSector sector = db.getWorld().getWorldSector(
				coords);
		if (sector != null) {
			setSelectedSector(sector);
		}
	}

}
