package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitMove;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitMoveRepo extends JpaRepository<UnitMove, Integer>, QuerydslPredicateExecutor<UnitMove> {
    void deleteByUnit(Unit unit);
}
