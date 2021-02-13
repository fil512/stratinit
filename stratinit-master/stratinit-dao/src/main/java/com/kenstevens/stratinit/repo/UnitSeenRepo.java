package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitSeen;
import com.kenstevens.stratinit.model.UnitSeenPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitSeenRepo extends JpaRepository<UnitSeen, UnitSeenPK> {
    @Query("select us from UnitSeen us where us.unitSeenPK.nation.nationPK.game = :game")
    List<UnitSeen> findByGame(@Param("game") Game game);

    @Modifying
    @Query("delete from UnitSeen us where us.unitSeenPK.unit = :unit")
    void deleteByUnit(@Param("unit") Unit unit);
}
