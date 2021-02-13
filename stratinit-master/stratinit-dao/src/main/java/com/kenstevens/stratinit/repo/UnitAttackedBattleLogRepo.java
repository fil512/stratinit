package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.UnitAttackedBattleLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitAttackedBattleLogRepo extends JpaRepository<UnitAttackedBattleLog, Integer> {
}
