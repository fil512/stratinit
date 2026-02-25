package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.client.model.audit.GameNationSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameNationSnapshotRepo extends JpaRepository<GameNationSnapshot, Integer> {
	List<GameNationSnapshot> findByGameIdOrderByTickNumberAscNationNameAsc(int gameId);
}
