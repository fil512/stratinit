package com.kenstevens.stratinit.main;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.remote.StratInit;

@Service
public class FlushCache implements ServletContextListener {
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private StratInit stratInit;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		logger.info("Shutdown event received");
		stratInit.shutdown();
		logger.info("Shutdown event processed");
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// Can't load cache here since dataCache won't be wired yet.
	}
}
