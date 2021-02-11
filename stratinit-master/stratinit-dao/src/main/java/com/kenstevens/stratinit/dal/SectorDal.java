package com.kenstevens.stratinit.dal;

import com.kenstevens.stratinit.model.*;

import java.util.Collection;
import java.util.List;

public interface SectorDal {

	List<City> getCities(Game game);

	List<SectorSeen> getSectorsSeen(Game game);

	World getWorld(Game game);

	void flushSectorsSeen(Collection<SectorSeen> sectorsSeen);

	void flush(City city);

	void persist(Sector sector);

	void persist(City city);

	void persist(SectorSeen sectorSeen);

	void persist(World world);

	void flushSectors(List<Sector> sectors);

	void remove(City city);

	List<CityMove> findCityMoves(City city);

	void flush(CityMove cityMove);

	void flushCityMoves(Collection<CityMove> cityMoves);

	List<CityMove> getCityMoves(Game game);

	void persist(CityMove cityMove);

	void remove(CityMove cityMove);
}
