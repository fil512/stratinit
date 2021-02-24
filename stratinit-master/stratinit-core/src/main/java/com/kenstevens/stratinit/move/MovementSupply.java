package com.kenstevens.stratinit.move;

import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MovementSupply {

    private final Unit unit;
    private final WorldView worldView;

    public MovementSupply(Unit unit, WorldView worldView) {
        this.unit = unit;
        this.worldView = worldView;
    }

	void addSupply() {
		List<WorldSector> sectors = worldView.getWorldSectors();
		if (!unit.isLand() && !unit.isNavy()
				|| unit.getType() == UnitType.PATROL) {
			for (WorldSector sector : sectors) {
				sector.setInSupply(true);
			}
			return;
		}
		Set<SectorCoords> suppliers;
		if (unit.isLand()) {
			suppliers = worldView.getLandSupplyLocations();
		} else {
			suppliers = worldView.getNavalSupplyLocations();
		}
		if (unit.getType() == UnitType.SUPPLY) {
			suppliers.remove(unit.getCoords());
		}
		Set<SectorCoords> suppliedLocations = expand(suppliers);
		for (WorldSector sector : sectors) {
            sector.setInSupply(suppliedLocations.contains(sector.getCoords()));
		}
	}

	private Set<SectorCoords> expand(Set<SectorCoords> suppliers) {
		Set<SectorCoords> retval = new HashSet<SectorCoords>();
		for (SectorCoords supplier : suppliers) {
            retval.addAll(supplier.sectorsWithin(worldView.size(),
                    Constants.SUPPLY_RADIUS, true));
        }
		return retval;
	}
}
