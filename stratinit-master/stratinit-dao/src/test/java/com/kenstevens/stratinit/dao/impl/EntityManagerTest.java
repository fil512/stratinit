package com.kenstevens.stratinit.dao.impl;

import com.kenstevens.stratinit.StratInitTest;
import org.hibernate.HibernateException;
import org.hibernate.internal.SessionImpl;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class EntityManagerTest extends StratInitTest {
	@PersistenceContext
	protected EntityManager entityManager;

	@Test
	public void isH2() throws HibernateException, SQLException {
		SessionImpl session = (SessionImpl) entityManager.getDelegate();
		assertTrue(session.getFactory().getDialect() instanceof
				org.hibernate.dialect.H2Dialect);
	}
}
