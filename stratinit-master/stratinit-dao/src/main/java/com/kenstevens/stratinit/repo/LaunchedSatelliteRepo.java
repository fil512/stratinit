package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.LaunchedSatellite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LaunchedSatelliteRepo extends JpaRepository<LaunchedSatellite, Integer> {
    @Query("Select u from LaunchedSatellite u WHERE u.nation.nationPK.game = :game")
    List<LaunchedSatellite> findByGame(@Param("game") Game game);
}
