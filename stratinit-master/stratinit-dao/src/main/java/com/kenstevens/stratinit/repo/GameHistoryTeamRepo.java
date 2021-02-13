package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.GameHistory;
import com.kenstevens.stratinit.model.GameHistoryTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameHistoryTeamRepo extends JpaRepository<GameHistoryTeam, Integer> {
    List<GameHistoryTeam> findByGameHistory(GameHistory gameHistory);
}
