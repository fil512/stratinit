package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.Mail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailRepo extends JpaRepository<Mail, Integer> {
}
