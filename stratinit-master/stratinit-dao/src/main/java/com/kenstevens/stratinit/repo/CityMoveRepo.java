package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.CityMove;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityMoveRepo extends JpaRepository<CityMove, Integer>, QuerydslPredicateExecutor<CityMove> {
    List<CityMove> findByCity(City city);
}
