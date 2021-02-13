package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.CityNukedBattleLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityNukedBattleLogRepo extends JpaRepository<CityNukedBattleLog, Integer> {
}
