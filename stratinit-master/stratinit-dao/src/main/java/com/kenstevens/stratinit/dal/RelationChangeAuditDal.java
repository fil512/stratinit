package com.kenstevens.stratinit.dal;

import com.kenstevens.stratinit.model.audit.RelationChangeAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelationChangeAuditDal extends JpaRepository<RelationChangeAudit, Integer> {
}
