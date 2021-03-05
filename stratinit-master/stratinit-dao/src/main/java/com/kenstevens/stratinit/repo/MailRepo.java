package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.client.model.Mail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MailRepo extends JpaRepository<Mail, Integer>, QuerydslPredicateExecutor<Mail> {
}
