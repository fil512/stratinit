package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.SectorPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SectorRepo extends JpaRepository<Sector, SectorPK>, QuerydslPredicateExecutor<Sector> {
}
