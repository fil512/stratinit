package com.kenstevens.stratinit.dao.impl;

import com.kenstevens.stratinit.StratInitTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class EntityManagerTest extends StratInitTest {
	@PersistenceContext
	protected EntityManager entityManager;

	// FIXME
//	@Test
//	public void isH2() throws HibernateException, SQLException {
//		SessionImpl session = (SessionImpl) entityManager.getDelegate();
//		assertTrue(session.getFactory().getDialect() instanceof
//				org.hibernate.dialect.H2Dialect);
//	}
}
