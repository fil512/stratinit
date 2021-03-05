package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.client.model.Relation;
import com.kenstevens.stratinit.client.model.RelationPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RelationRepo extends JpaRepository<Relation, RelationPK>, QuerydslPredicateExecutor<Relation> {
}
