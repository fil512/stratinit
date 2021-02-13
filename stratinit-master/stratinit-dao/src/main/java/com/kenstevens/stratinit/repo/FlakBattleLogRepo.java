package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.FlakBattleLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlakBattleLogRepo extends JpaRepository<FlakBattleLog, Integer> {
}
