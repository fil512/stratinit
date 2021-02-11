package com.kenstevens.stratinit.dal;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.CityPK;
import com.kenstevens.stratinit.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityDal extends JpaRepository<City, CityPK> {
    List<City> findByCityPKGame(Game game);
}
