package com.kenstevens.stratinit.dal;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NationDal extends JpaRepository<Nation, Integer> {
    List<Nation> findByNationPKGame(Game game);
}