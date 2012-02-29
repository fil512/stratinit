package com.kenstevens.stratinit.site.processor;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.LaunchedSatellite;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.type.Constants;

@Service
public class FogOfWar {
	@Autowired
	private Data db;

	public void setFogOfWar() {
		WorldView worldView = db.getWorld();
		
		for (Unit unit : db.getUnitList()) {
			seeFromUnits(worldView, unit);	
		}
		for (Unit unit : db.getSeenUnitList()) {
			if (unit.getNation().equals(db.getAlly())) {
				seeFromUnits(worldView, unit);
			}
		}
		for (LaunchedSatellite sat : db.getSatelliteList()) {
			for (WorldSector worldSector : worldView.getNeighbours(sat.getCoords(), Constants.SATELLITE_SIGHT, true)) {
				worldSector.setVisible(true);
			}	
		}
		for (City city : db.getCityList()) {
			for (WorldSector worldSector : worldView.getNeighbours(city.getCoords(), city.getSightRadius(), true)) {
				worldSector.setVisible(true);
			}
		}
		addAllyCities(worldView);
	}

	private Collection<WorldSector> addAllyCities(WorldView worldView) {
		Collection<WorldSector> allyCitySectors = Lists.newArrayList();
		for (WorldSector sector : worldView.getWorldSectors()) {
			if (sector.isPlayerCity() && sector.getNation().equals(db.getAlly())) {
				for (WorldSector worldSector : worldView.getNeighbours(sector.getCoords(), Constants.CITY_VIEW_DISTANCE, true)) {
					worldSector.setVisible(true);
				}
			}
		}
		return allyCitySectors;
	}

	private void seeFromUnits(WorldView worldView, Unit unit) {
		for (WorldSector worldSector : worldView.getNeighbours(unit.getCoords(), unit.getSight(), true)) {
			worldSector.setVisible(true);
		}
	}
}
