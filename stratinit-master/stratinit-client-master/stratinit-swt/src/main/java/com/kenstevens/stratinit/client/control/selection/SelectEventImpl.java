package com.kenstevens.stratinit.client.control.selection;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.kenstevens.stratinit.client.event.StratinitEventBus;
import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.type.SectorCoords;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SelectEventImpl implements SelectEvent {
	@Autowired
	private SelectedUnits selectedUnits;
	@Autowired
	private SelectedCoords selectedCoords;
	@Autowired
	private SelectedNation selectedNation;
	@Autowired
	private SelectedCity selectedCity;
	@Autowired
	private Data db;
	@Autowired
	private StratinitEventBus eventBus;

	@Override
	public void selectSectorCoords(SectorCoords sectorCoords,
								   Selection.Source selectionSource) {
		selectSectorCoords(sectorCoords, selectionSource, true);
	}

	@Override
	public void selectSectorCoordsNoFire(SectorCoords sectorCoords,
										 Selection.Source selectionSource) {
		selectSectorCoords(sectorCoords, selectionSource, false);
	}

	@Override
	public void reSelectSectorCoords(final Selection.Source selectionSource) {
		Display display = Display.getDefault();

		if (display.isDisposed())
			return;
		display.asyncExec(new Runnable() {
			public void run() {
				selectSectorCoords(selectedCoords.getCoords(), selectionSource,
						true);
			}
		});
	}

	@Override
	public void selectSectorCoords(SectorCoords sectorCoords,
								   Selection.Source selectionSource, boolean fireEvent) {
		selectedUnits.clear();
		selectCoords(sectorCoords);
		if (fireEvent) {
			eventBus.post(new SelectSectorEvent(selectionSource));
		}
	}

	@Override
	public void selectUnits(List<UnitView> units,
							Selection.Source selectionSource) {
		selectUnits(units, selectionSource, true);
	}

	@Override
	public void selectUnitsWithMobNoFire(Selection.Source selectionSource) {
		List<UnitView> unitsWithMob = Lists.newArrayList(Iterables.filter(
				selectedUnits, new Predicate<Unit>() {
					@Override
					public boolean apply(Unit unit) {
						return unit.getMobility() > 0;
					}
				}));
		if (unitsWithMob.isEmpty()) {
			if (selectedUnits.isEmpty()) {
				selectSectorCoords(selectedUnits.getCoords(), selectionSource,
						false);
			} else {
				selectUnits(Lists.newArrayList(selectedUnits), selectionSource,
						false);
			}
		} else {
			selectUnits(unitsWithMob, selectionSource, false);
		}
	}

	@Override
	public void selectUnits(List<UnitView> units,
							Selection.Source selectionSource, boolean fireEvent) {
		if (units == null || units.isEmpty()) {
			return;
		}
		selectedUnits.setUnits(units);
		SectorCoords coords = units.get(0).getCoords();
		selectCoords(coords);
		if (fireEvent) {
			eventBus.post(new SelectUnitsEvent(selectionSource));
		}
	}

	@Override
	public void selectCoords(SectorCoords coords) {
		if (coords == null) {
			return;
		}
		selectedCoords.setCoords(coords);
		selectedCity.setCity(db.getCity(coords));
	}

	@Override
	public void selectUnit(UnitView unit, Selection.Source selectionSource) {
		List<UnitView> units = new ArrayList<UnitView>();
		units.add(unit);
		selectUnits(units, selectionSource);
	}

	@Override
	public void selectNation(NationView nation, Selection.Source selectionSource) {
		selectedNation.setNation(nation);
		eventBus.post(new SelectNationEvent(selectionSource));
	}

	@Override
	public List<UnitView> getSelectedUnits() {
		return Lists.newArrayList(selectedUnits);
	}

}
