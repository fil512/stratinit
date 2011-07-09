package com.kenstevens.stratinit.wicket;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

// FIXME why is this using the test persistance.xml and not the prod one?
public class Start {

	public static void main(String[] args) throws Exception {
		System.setProperty("com.kenstevens.stratinit.mail", "disable");
		Server server = new Server();
		SocketConnector connector = new SocketConnector();

		// Set some timeout options to make debugging easier.
		connector.setMaxIdleTime(1000 * 60 * 60);
		connector.setSoLingerTime(-1);
		connector.setPort(9999);
		server.setConnectors(new Connector[] { connector });

		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setServer(server);
//		webAppContext.setContextPath("/");
		webAppContext.setWar("src/main/webapp");

		// START JMX SERVER
		// MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		// MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
		// server.getContainer().addEventListener(mBeanContainer);
		// mBeanContainer.start();

		server.addHandler(webAppContext);
		
//		setupInitialContextFactory();

		try {
			System.out.println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP");
			server.start();
			System.in.read();
			System.out.println(">>> STOPPING EMBEDDED JETTY SERVER");
            // while (System.in.available() == 0) {
			//   Thread.sleep(5000);
			// }
			server.stop();
			server.join();
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(100);
		}
	}
//
//	private static void setupInitialContextFactory() {
//		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "ca.intelliware.kinetic.wicket.jndi.InitialContextFactory");
//	}
	

}
