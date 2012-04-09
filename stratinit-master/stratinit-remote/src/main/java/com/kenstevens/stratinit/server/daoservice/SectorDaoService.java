package com.kenstevens.stratinit.server.daoservice;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.CityPK;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.LaunchedSatellite;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.UpdateCityField;
import com.kenstevens.stratinit.type.CoordMeasure;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

public interface SectorDaoService {

	void survey(Nation nation);

	// Returns true if build type changed
	void buildUnit(CityPK cityPK, Date buildTime);

	// TODO DEPL persistence.xml read-only on getters
	WorldView getAllWorldView(Nation nation);

	WorldView getSupplyWorldView(Unit unit);

	WorldView getInterdictionWorldView(Unit unit, Nation nation);

	WorldView getInterceptionWorldView(SectorCoords coords,
			Nation nation);

	WorldView getSeenWorldView(Nation nation);

	int getLandUnitWeight(WorldSector worldSector);

	List<Sector> getSectorsSeen(Unit unit);

	void merge(City city);

	// Returns old owner
	Nation captureCity(Nation nation, Sector sector);

	void explodeSupply(Nation nation, SectorCoords coords);

	void updateSeen(CoordMeasure coordMeasure,
			List<Unit> units, MoveSeen moveSeen);

	void unitsSeen(CoordMeasure coordMeasure, List<Unit> units,
			MoveSeen moveSeen, boolean attacking);

	void satelliteSees(LaunchedSatellite satellite,
			MoveSeen moveSeen);

	WorldSector refreshWorldSector(Nation nation,
			WorldView worldView, WorldSector targetSector);

	Nation captureCity(Nation nation, SectorCoords city);

	Set<Nation> devastate(Unit attackerUnit, Sector sector, boolean isCounter);

	Result<City> updateCity(Nation nation, SectorCoords coords, UpdateCityField field, UnitType build,
			UnitType nextBuild, boolean switchOnTechChange, SectorCoords nextCoords);

	void saveIfNew(Nation nation, Sector sector);

	void unitSeen(CoordMeasure coordMeasure, Unit unit,
			MoveSeen moveSeen, boolean attacking);

	void updateSeen(CoordMeasure coordMeasure, Unit unit,
			MoveSeen moveSeen);

	Result<None> cedeCity(City city, Nation nation);

	void switchCityBuildsFromTechChange(Nation nation, Date buildTime);

	Result<None> establishCity(Unit unit);

	Result<None> destroyCity(City city);

	void cityChanged(City city);

	void merge(Sector sector);

	void removeCityMoves(Game game);

	void setCityMove(City city, SectorCoords targetCoords);
	
	void remove(City city);

	WorldSector getSectorView(Unit unit);
}