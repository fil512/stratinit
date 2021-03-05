package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.client.model.GameHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameHistoryRepo extends JpaRepository<GameHistory, Integer> {
    GameHistory findByGameId(Integer id);
}
