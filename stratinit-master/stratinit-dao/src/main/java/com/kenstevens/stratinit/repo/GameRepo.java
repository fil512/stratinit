package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepo extends JpaRepository<Game, Integer>, QuerydslPredicateExecutor<Game> {
}
