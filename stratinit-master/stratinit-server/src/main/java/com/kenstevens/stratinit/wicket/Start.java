package com.kenstevens.stratinit.wicket;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

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
		webAppContext.setWar("src/main/webapp");

		server.addHandler(webAppContext);

		System.out
				.println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP");
		server.start();
		System.in.read();
		System.out.println(">>> STOPPING EMBEDDED JETTY SERVER");
		server.stop();
		server.join();
	}

}
