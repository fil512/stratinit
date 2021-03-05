package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.client.model.audit.RelationChangeAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RelationChangeAuditRepo extends JpaRepository<RelationChangeAudit, Integer>, QuerydslPredicateExecutor<RelationChangeAudit> {
}
