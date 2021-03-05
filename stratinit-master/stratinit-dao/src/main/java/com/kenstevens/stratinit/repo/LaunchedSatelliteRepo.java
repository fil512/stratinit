package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.client.model.LaunchedSatellite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LaunchedSatelliteRepo extends JpaRepository<LaunchedSatellite, Integer>, QuerydslPredicateExecutor<LaunchedSatellite> {
}
