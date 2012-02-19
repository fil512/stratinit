package com.kenstevens.stratinit.server.remote.commands;

import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.impl.SessionImpl;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.server.remote.AuthenticationHelper;

@RunWith(SpringJUnit4ClassRunner.class)
//Move ddl generation up to web project so there is only one real persistence.xml
//move proper persistence.xml up here
@ContextConfiguration(locations = "/WEB-INF/applicationContext.xml")
@TransactionConfiguration(transactionManager = "txManager")
@Transactional
public abstract class GWTTestBase {
	@PersistenceContext
	protected EntityManager entityManager;
	@Autowired protected GameDao gameDao;
	@Autowired protected PlayerDao playerDao;
	@Autowired protected DataCache dataCache;
	protected final String PLAYER_ME_NAME = "me";
	protected Player playerMe;
	private boolean initialized = false;

	protected void setAuthentication(String username) {
		new AuthenticationHelper().setAuthentication(username);
	}

	@Before
	public void addPlayerMe() {
		if (!initialized ) {
			SessionImpl session = getSession();
			assertTrue("Running in HSQL", session.getFactory().getSettings().getDialect() instanceof org.hibernate.dialect.HSQLDialect);
			initialized = true;
		}

		playerMe = new Player(PLAYER_ME_NAME);
		playerDao.persist(playerMe);
		entityManager.flush();
		setAuthentication(PLAYER_ME_NAME);
	}

	private SessionImpl getSession() {
		return (SessionImpl) entityManager.getDelegate();
	}
}
