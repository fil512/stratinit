package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.client.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepo extends JpaRepository<Game, Integer>, QuerydslPredicateExecutor<Game> {
    List<Game> findByEnabledTrue();
}
