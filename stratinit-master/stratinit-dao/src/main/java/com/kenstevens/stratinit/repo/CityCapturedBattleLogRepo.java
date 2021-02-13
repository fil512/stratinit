package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.CityCapturedBattleLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityCapturedBattleLogRepo extends JpaRepository<CityCapturedBattleLog, Integer> {
}
