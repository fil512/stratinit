package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.BattleLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BattleLogRepo extends JpaRepository<BattleLog, Integer> {
}
