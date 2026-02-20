package com.kenstevens.stratinit.dao.impl;

import com.kenstevens.stratinit.StratInitTest;
import org.hibernate.internal.SessionImpl;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EntityManagerTest extends StratInitTest {
	@PersistenceContext
	protected EntityManager entityManager;

	@Test
	public void isH2() {
		SessionImpl session = (SessionImpl) entityManager.getDelegate();
		assertTrue(session.getFactory().getJdbcServices().getDialect() instanceof
				org.hibernate.dialect.H2Dialect);
	}
}
