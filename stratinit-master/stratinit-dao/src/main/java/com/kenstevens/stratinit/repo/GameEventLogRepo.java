package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.client.model.audit.GameEventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameEventLogRepo extends JpaRepository<GameEventLog, Integer> {
	List<GameEventLog> findByGameIdOrderByEventTimeDesc(int gameId);
	List<GameEventLog> findByGameIdAndNationNameOrderByEventTimeDesc(int gameId, String nationName);
}
