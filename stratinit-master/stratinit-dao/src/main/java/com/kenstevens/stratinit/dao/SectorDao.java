package com.kenstevens.stratinit.dao;

import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.type.SectorCoords;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SectorDao {

	void save(SectorSeen sectorSeen);

	List<City> getCities(Nation nation);

	Collection<SectorSeen> getSectorsSeen(Nation nation);

	Collection<Sector> getStartCitiesOnIsland(Game game, int island);

	Collection<City> getCities(Game game);

	City getCity(Sector sector);

	City getCity(Nation nation, SectorCoords coords);

	void save(World world);

	City findCity(CityPK cityPK);

	SectorSeen findSectorSeen(Nation nation, SectorCoords coords);

	int getNumberOfCities(Nation nation);

	long getNumberOfTechCentres(Nation nation);

	void markCacheModified(City city);

	Collection<Sector> getNationSectorsSeenSectors(Nation nation);

	void markCacheModified(SectorSeen sectorSeen);

	Collection<Nation> getOtherNationsThatSeeThisSector(
			Nation nation, SectorCoords coords);

	SectorSeen findSectorSeen(Nation nation, Sector sector);

	// TODO REF rename to make consistent with unitSectorSeen
	void saveIfNew(Nation nation, Sector sector);

	List<City> getSeenCities(Nation nation);

	void markCacheModified(Sector sector);

	void delete(City city);

	World getWorld(Game game);

	Map<SectorCoords, City> getCityMap(Game game);

	void save(City city);

	void transferCity(City city, Nation nation);

	int getNumberOfBases(Nation nation);

	boolean canEstablishCity(Nation nation, Sector sector);

	void clearCityMove(City city);

	List<CityMove> getCityMoves(Game game);

	void markCacheModified(CityMove cityMove);

	void save(CityMove cityMove);

	void delete(CityMove cityMove);
}