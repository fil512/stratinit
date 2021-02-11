package com.kenstevens.stratinit.dal;

import com.kenstevens.stratinit.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameDal extends JpaRepository<Game, Integer> {
}
