package com.kenstevens.stratinit.dao;

import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import org.junit.Test;

public class ApplicationContextTest {
	@Test
	public void getAppContext() {
		InputStream context = this.getClass().getResourceAsStream("/WEB-INF/applicationContext.xml");
		assertNotNull("Unable to load application context", context);
	}
}
