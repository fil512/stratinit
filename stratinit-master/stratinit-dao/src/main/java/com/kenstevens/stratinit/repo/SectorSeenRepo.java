package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.client.model.SectorSeen;
import com.kenstevens.stratinit.client.model.SectorSeenPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SectorSeenRepo extends JpaRepository<SectorSeen, SectorSeenPK>, QuerydslPredicateExecutor<SectorSeen> {
}
