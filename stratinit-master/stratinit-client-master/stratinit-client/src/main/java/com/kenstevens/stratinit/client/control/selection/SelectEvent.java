package com.kenstevens.stratinit.client.control.selection;

import com.kenstevens.stratinit.client.model.NationView;
import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.type.SectorCoords;

import java.util.List;

// FIXME rename and move to api
public interface SelectEvent {
    void reSelectSectorCoords(Selection.Source canvasSelect);

    void selectSectorCoords(SectorCoords sectorCoords,
                            Selection.Source selectionSource);

    void selectSectorCoordsNoFire(SectorCoords target, Selection.Source canvasSelect);

    void selectSectorCoords(SectorCoords sectorCoords,
                            Selection.Source selectionSource, boolean fireEvent);

    void selectUnits(List<UnitView> units,
                     Selection.Source selectionSource);

    void selectUnitsWithMobNoFire(Selection.Source selectionSource);

    void selectUnits(List<UnitView> units,
                     Selection.Source selectionSource, boolean fireEvent);

    void selectCoords(SectorCoords coords);

    void selectUnit(UnitView unit, Selection.Source selectionSource);

    void selectNation(NationView nation, Selection.Source selectionSource);

    List<UnitView> getSelectedUnits();
}
