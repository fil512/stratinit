package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.CityPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepo extends JpaRepository<City, CityPK>, QuerydslPredicateExecutor<City> {
}
