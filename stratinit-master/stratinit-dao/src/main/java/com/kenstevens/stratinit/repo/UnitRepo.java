package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepo extends JpaRepository<Unit, Integer> {
    @Query("select u from Unit u WHERE u.nation.nationPK.game = :game")
    List<Unit> findByGame(@Param("game") Game game);

    @Modifying
    @Query("delete from Unit u WHERE u.nation.nationPK.game = :game")
    void deleteByGame(@Param("game") Game game);
}
