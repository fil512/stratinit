package com.kenstevens.stratinit.dal;

import com.kenstevens.stratinit.model.GameBuildAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameBuildAuditDal extends JpaRepository<GameBuildAudit, String> {
}
