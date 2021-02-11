package com.kenstevens.stratinit.dal;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Relation;
import com.kenstevens.stratinit.model.RelationPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationDal extends JpaRepository<Relation, RelationPK> {
    @Query("select r from Relation r where r.relationPK.from.nationPK.game = :game")
    List<Relation> findByGame(@Param("game") Game game);
}
