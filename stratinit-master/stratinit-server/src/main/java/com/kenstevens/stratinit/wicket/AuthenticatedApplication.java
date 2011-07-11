package com.kenstevens.stratinit.wicket;

import org.apache.wicket.Application;
import org.apache.wicket.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.server.daoservice.PlayerDaoService;
import com.kenstevens.stratinit.spring.SpringContext;
import com.kenstevens.stratinit.wicket.admin.AdminPage;
import com.kenstevens.stratinit.wicket.admin.PostPage;

/**
 * AuthenticatedApplication object for your web application. If you want to run
 * this application without deploying, run the Start class.
 *
 * @see ca.intelliware.kinetic.wicket.development.Start#main(String[])
 */
public class AuthenticatedApplication extends AuthenticatedWebApplication {
	public static AuthenticatedApplication get() {
		return (AuthenticatedApplication) Application.get();
	}

	// FIXME wire this
	private PlayerDaoService playerDaoService;
	private PlayerDao playerDao;

	@Override
	protected void init() {
		super.init();
		
		getComponentInstantiationListeners().add(new SpringComponentInjector(this));
		mountPage("/admin/AdminPage", AdminPage.class);
		mountPage("/admin/PostPage", PostPage.class);
		
		getApplicationSettings().setInternalErrorPage(ErrorPage.class);
		getApplicationSettings().setPageExpiredErrorPage(PageExpired.class);
		wireMe();
	}

	private void wireMe() {
		playerDao = (PlayerDao) SpringContext.getBean("playerDaoImpl");
		playerDaoService = (PlayerDaoService) SpringContext.getBean("playerDaoServiceImpl");
	}

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	public Class<HomePage> getHomePage() {
		return HomePage.class;
	}

	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		return LoginPage.class;
	}

	@Override
	protected Class<? extends AuthenticatedWebSession> getWebSessionClass() {
		return AuthenticatedSession.class;
	}

	public PlayerDaoService getPlayerDaoService() {
		return playerDaoService;
	}

	public void setPlayerDao(PlayerDaoService playerDaoService) {
		this.playerDaoService = playerDaoService;
	}

	public PlayerDao getPlayerDao() {
		return playerDao;
	}

	public void setPlayerDao(PlayerDao playerDao) {
		this.playerDao = playerDao;
	}
}
