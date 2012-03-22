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

	public abstract void survey(Nation nation);

	// Returns true if build type changed
	public abstract void buildUnit(CityPK cityPK, Date buildTime);

	// TODO DEPL persistence.xml read-only on getters
	public abstract WorldView getAllWorldView(Nation nation);

	public abstract WorldView getSupplyWorldView(Unit unit);

	public abstract WorldView getInterdictionWorldView(Unit unit, Nation nation);

	public abstract WorldView getInterceptionWorldView(SectorCoords coords,
			Nation nation);

	public abstract WorldView getSeenWorldView(Nation nation);

	public abstract int getLandUnitWeight(WorldSector worldSector);

	public abstract List<Sector> getSectorsSeen(Unit unit);

	public abstract void merge(City city);

	// Returns old owner
	public abstract Nation captureCity(Nation nation, Sector sector);

	public abstract void explodeSupply(Nation nation, SectorCoords coords);

	public abstract void updateSeen(CoordMeasure coordMeasure,
			List<Unit> units, MoveSeen moveSeen);

	public abstract void unitsSeen(CoordMeasure coordMeasure, List<Unit> units,
			MoveSeen moveSeen, boolean attacking);

	public abstract void satelliteSees(LaunchedSatellite satellite,
			MoveSeen moveSeen);

	public abstract WorldSector refreshWorldSector(Nation nation,
			WorldView worldView, WorldSector targetSector);

	public abstract Nation captureCity(Nation nation, SectorCoords city);

	public abstract Set<Nation> devastate(Unit attackerUnit, Sector sector, boolean isCounter);

	public abstract Result<City> updateCity(Nation nation, SectorCoords coords, UpdateCityField field, UnitType build,
			UnitType nextBuild, boolean switchOnTechChange, SectorCoords nextCoords);

	public abstract void saveIfNew(Nation nation, Sector sector);

	public abstract void unitSeen(CoordMeasure coordMeasure, Unit unit,
			MoveSeen moveSeen, boolean attacking);

	public abstract void updateSeen(CoordMeasure coordMeasure, Unit unit,
			MoveSeen moveSeen);

	public abstract Result<None> cedeCity(City city, Nation nation);

	public abstract void switchCityBuildsFromTechChange(Nation nation, Date buildTime);

	public abstract Result<None> establishCity(Unit unit);

	public abstract Result<None> destroyCity(City city);

	public abstract void cityChanged(City city);

	public abstract void merge(Sector sector);

	void removeCityMoves(Game game);

	void setCityMove(City city, SectorCoords targetCoords);
	
	public abstract void remove(City city);

	WorldSector getSectorView(Unit unit);
}