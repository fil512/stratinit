package com.kenstevens.stratinit.dao.impl;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.HibernateException;
import org.hibernate.internal.SessionImpl;
import org.junit.Test;

import com.kenstevens.stratinit.StratInitTest;

public class EntityManagerTest extends StratInitTest {
	@PersistenceContext
	protected EntityManager entityManager;
	
	@Test
	public void isHSQL() throws HibernateException, SQLException {
		SessionImpl session = (SessionImpl)entityManager.getDelegate();
		assertTrue(session.getFactory().getDialect() instanceof 
				org.hibernate.dialect.HSQLDialect);
	}
}
