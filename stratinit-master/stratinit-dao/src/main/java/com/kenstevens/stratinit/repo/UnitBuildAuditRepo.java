package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.audit.UnitBuildAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitBuildAuditRepo extends JpaRepository<UnitBuildAudit, Integer> {
    List<UnitBuildAudit> findByGameId(int gameId);

    List<UnitBuildAudit> findByGameIdAndUsername(int gameId, String username);
}
