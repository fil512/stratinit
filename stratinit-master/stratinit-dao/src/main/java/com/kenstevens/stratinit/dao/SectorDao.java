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

	void persist(SectorSeen sectorSeen);

	List<City> getCities(Nation nation);

	List<City> getAllCities();

	Collection<SectorSeen> getSectorsSeen(Nation nation);

	Collection<Sector> getStartCitiesOnIsland(Game game, int island);

	Collection<City> getCities(Game game);

	City getCity(Sector sector);

	City getCity(Nation nation, SectorCoords coords);

	void persist(World world);

	City findCity(CityPK cityPK);

	SectorSeen findSectorSeen(Nation nation, SectorCoords coords);

	int getNumberOfCities(Nation nation);

	long getNumberOfTechCentres(Nation nation);

	void merge(City city);

	Collection<Sector> getNationSectorsSeenSectors(Nation nation);

	void merge(SectorSeen sectorSeen);

	Collection<Nation> getOtherNationsThatSeeThisSector(
			Nation nation, SectorCoords coords);

	SectorSeen findSectorSeen(Nation nation, Sector sector);

	// TODO REF rename to make consistent with unitSectorSeen
	void saveIfNew(Nation nation, Sector sector);

	List<City> getSeenCities(Nation nation);

	void merge(Sector sector);

	void remove(City city);

	World getWorld(int gameId);

	Map<SectorCoords, City> getCityMap(Game game);

	void persist(City city);

	void transferCity(City city, Nation nation);

	int getNumberOfBases(Nation nation);

	boolean canEstablishCity(Nation nation, Sector sector);

	void clearCityMove(City city);

	List<CityMove> getCityMoves(Game game);

	void merge(CityMove cityMove);

	void persist(CityMove cityMove);

	void remove(CityMove cityMove);
}