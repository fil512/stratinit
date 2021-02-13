package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.SectorPK;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectorRepo extends JpaRepository<Sector, SectorPK> {
	@Query("select s from Sector s WHERE s.sectorPK.game = :game")
	List<Sector> findByGame(@Param("game") Game game);

	@Query("select s from Sector s WHERE s.sectorPK.game = :game and s.sectorPK.coords = :coords")
	Sector findByGameAndCoords(@Param("game") Game game, @Param("coords") SectorCoords coords);

	@Modifying
	@Query("delete from Sector s WHERE s.sectorPK.game = :game")
	void deleteByGame(@Param("game") Game game);
}
