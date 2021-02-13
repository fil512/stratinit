package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitMove;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitMoveRepo extends JpaRepository<UnitMove, Integer> {
    @Query("Select um from UnitMove um WHERE um.unit.nation.nationPK.game = :game")
    List<UnitMove> findByGame(@Param("game") Game game);

    void deleteByUnit(Unit unit);
}
