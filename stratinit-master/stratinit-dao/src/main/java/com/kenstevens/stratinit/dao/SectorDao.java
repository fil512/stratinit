package com.kenstevens.stratinit.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.CityMove;
import com.kenstevens.stratinit.model.CityPK;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.SectorSeen;
import com.kenstevens.stratinit.model.World;
import com.kenstevens.stratinit.type.SectorCoords;

public interface SectorDao {

	public abstract void persist(SectorSeen sectorSeen);

	public abstract List<City> getCities(Nation nation);

	public abstract List<City> getAllCities();

	public abstract Collection<SectorSeen> getSectorsSeen(Nation nation);

	public abstract Collection<Sector> getStartCitiesOnIsland(Game game, int island);

	public abstract Collection<City> getCities(Game game);

	public abstract City getCity(Sector sector);

	public abstract City getCity(Nation nation, SectorCoords coords);

	public abstract void persist(World world);

	public abstract City findCity(CityPK cityPK);

	public abstract SectorSeen findSectorSeen(Nation nation, SectorCoords coords);

	public abstract int getNumberOfCities(Nation nation);

	public abstract long getNumberOfTechCentres(Nation nation);

	public abstract void merge(City city);

	public abstract Collection<Sector> getNationSectorsSeenSectors(Nation nation);

	public abstract void merge(SectorSeen sectorSeen);

	public abstract Collection<Nation> getOtherNationsThatSeeThisSector(
			Nation nation, SectorCoords coords);

	public abstract SectorSeen findSectorSeen(Nation nation, Sector sector);

	// TODO REF rename to make consistent with unitSectorSeen
	public abstract void saveIfNew(Nation nation, Sector sector);

	public abstract List<City> getSeenCities(Nation nation);

	public abstract void merge(Sector sector);

	public abstract void remove(City city);

	public abstract World getWorld(int gameId);

	public abstract Map<SectorCoords, City> getCityMap(Game game);

	void persist(City city);

	public abstract void transferCity(City city, Nation nation);

	int getNumberOfBases(Nation nation);

	public abstract boolean canEstablishCity(Nation nation, Sector sector);

	void clearCityMove(City city);

	List<CityMove> getCityMoves(Game game);

	void merge(CityMove cityMove);

	void persist(CityMove cityMove);

	void remove(CityMove cityMove);
}