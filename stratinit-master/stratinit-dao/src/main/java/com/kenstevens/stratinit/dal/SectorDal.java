package com.kenstevens.stratinit.dal;

import java.util.Collection;
import java.util.List;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.CityMove;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.SectorSeen;
import com.kenstevens.stratinit.model.World;

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

	void remove(Game game);

	void flushSectors(List<Sector> sectors);

	void remove(City city);

	List<CityMove> findCityMoves(City city);

	void flush(CityMove cityMove);

	void flushCityMoves(Collection<CityMove> cityMoves);

	List<CityMove> getCityMoves(Game game);

	void persist(CityMove cityMove);

	void remove(CityMove cityMove);
}
