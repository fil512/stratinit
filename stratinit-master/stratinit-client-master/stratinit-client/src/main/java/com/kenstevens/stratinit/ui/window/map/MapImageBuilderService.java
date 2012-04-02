package com.kenstevens.stratinit.ui.window.map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.model.SelectedUnits;
import com.kenstevens.stratinit.supply.Supply;
import com.kenstevens.stratinit.ui.window.MapImageManager;
import com.kenstevens.stratinit.ui.window.map.drawer.MapBuilder;

@Service
public class MapImageBuilderService {
	@Autowired
	MapImageManager mapImageManager;
	@Autowired
	SelectedUnits selectedUnits;
	@Autowired
	SectorDrawerService sectorDrawer;
	@Autowired
	UnitDrawerService unitDrawer;

	public Supply buildMapImage() {
		MapBuilder mapBuilder = new MapBuilder(mapImageManager);
		mapBuilder.draw();
		return mapBuilder.getSupply();
	}

	public void buildTopLayer(Supply supply) {
		if (selectedUnits.isEmpty()) {
			sectorDrawer.displayActiveLocation();
		} else {
			unitDrawer.drawSelectedUnitRange(supply);
		}

		// TODO Auto-generated method stub

	}
}
