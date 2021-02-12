package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.CityPK;
import com.kenstevens.stratinit.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepo extends JpaRepository<City, CityPK> {
    List<City> findByCityPKGame(Game game);
}
