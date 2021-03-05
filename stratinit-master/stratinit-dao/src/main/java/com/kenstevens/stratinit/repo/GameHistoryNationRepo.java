package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.client.model.GameHistoryNation;
import com.kenstevens.stratinit.client.model.GameHistoryTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameHistoryNationRepo extends JpaRepository<GameHistoryNation, Integer> {
    List<GameHistoryNation> findByGameHistoryTeam(GameHistoryTeam gameHistoryTeam);
}
