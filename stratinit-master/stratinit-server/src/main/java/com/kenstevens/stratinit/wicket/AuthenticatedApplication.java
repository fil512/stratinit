package com.kenstevens.stratinit.wicket;

import org.apache.wicket.Application;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

import com.kenstevens.stratinit.wicket.admin.AdminPage;
import com.kenstevens.stratinit.wicket.docs.AboutPage;
import com.kenstevens.stratinit.wicket.rank.LeaderBoardPage;

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

	@Override
	protected void init() {
		super.init();
		
		getComponentInstantiationListeners().add(new SpringComponentInjector(this));
		mountPackage("/admin", AdminPage.class);
		mountPackage("/docs", AboutPage.class);
		mountPackage("/games", LeaderBoardPage.class);
		getApplicationSettings().setInternalErrorPage(ErrorPage.class);
		getApplicationSettings().setPageExpiredErrorPage(PageExpired.class);
		
	}

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

	// FIXME
	@Override
	public RuntimeConfigurationType getConfigurationType() {
		return RuntimeConfigurationType.DEVELOPMENT;
	}
}
