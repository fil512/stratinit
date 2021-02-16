package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.SectorPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SectorRepo extends JpaRepository<Sector, SectorPK>, QuerydslPredicateExecutor<Sector> {
	// FIXME finish converting the repos to querydsl
	@Modifying
	@Query("delete from Sector s WHERE s.sectorPK.game = :game")
	void deleteByGame(@Param("game") Game game);
}
