package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.GameBuildAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameBuildAuditRepo extends JpaRepository<GameBuildAudit, String> {
}
