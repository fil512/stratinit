package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.client.model.Nation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface NationRepo extends JpaRepository<Nation, Integer>, QuerydslPredicateExecutor<Nation> {
}
