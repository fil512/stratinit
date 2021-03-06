package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.client.model.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorLogRepo extends JpaRepository<ErrorLog, Integer>, QuerydslPredicateExecutor<ErrorLog> {
}
