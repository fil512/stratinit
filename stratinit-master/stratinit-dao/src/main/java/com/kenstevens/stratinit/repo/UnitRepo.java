package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitRepo extends JpaRepository<Unit, Integer>, QuerydslPredicateExecutor<Unit> {
}
