package com.kenstevens.stratinit.wicket;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

import com.kenstevens.stratinit.wicket.admin.AdminPage;
import com.kenstevens.stratinit.wicket.admin.PostPage;

public class WicketApplication extends WebApplication
{    	
	@Override
	public Class<HomePage> getHomePage()
	{
		return HomePage.class;
	}

	@Override
	public void init()
	{
		super.init();
		getComponentInstantiationListeners().add(new SpringComponentInjector(this));
		mountPage("/admin/AdminPage", AdminPage.class);
		mountPage("/admin/PostPage", PostPage.class);
	}
}
