package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.CityMove;
import com.kenstevens.stratinit.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityMoveRepo extends JpaRepository<CityMove, Integer> {
    List<CityMove> findByCity(City city);

    @Query("select cm from CityMove cm where cm.city.nation.nationPK.game = :game")
    List<CityMove> findByGame(@Param("game") Game game);
}
