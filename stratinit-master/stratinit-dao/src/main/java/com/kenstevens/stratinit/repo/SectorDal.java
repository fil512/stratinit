package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SectorDal extends JpaRepository<Sector, SectorPK> {

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

	List<CityMove> getCityMoves(Game game);

	void persist(CityMove cityMove);

	void remove(CityMove cityMove);

	@Query("select s from Sector s WHERE s.sectorPK.game = :game")
	List<Sector> findByGame(@Param("game") Game game);
}
