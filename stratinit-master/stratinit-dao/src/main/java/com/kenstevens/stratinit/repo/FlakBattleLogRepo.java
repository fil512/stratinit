package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.client.model.FlakBattleLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FlakBattleLogRepo extends JpaRepository<FlakBattleLog, Integer>, QuerydslPredicateExecutor<FlakBattleLog> {
}
