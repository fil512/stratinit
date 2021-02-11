package com.kenstevens.stratinit.wicket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;

public final class Start {
    private static final Log LOGGER = LogFactory.getLog(Start.class);

    private Start() {
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("com.kenstevens.stratinit.mail", "disable");

        String webdir = "src/main/webapp";

        Server server = new Server(9999);
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(9999);


        // Set some timeout options to make debugging easier.
        connector.setIdleTimeout(1000 * 60 * 60);
        connector.setPort(9999);
        server.setConnectors(new Connector[]{connector});

        WebAppContext wcon = new WebAppContext();

//		wcon.setContextPath("/simserv");
//		wcon.setDescriptor(webdir + "/WEB-INF/web.xml");
//		wcon.setResourceBase(webdir);
        wcon.setWar(webdir);

        wcon.setParentLoaderPriority(true);

        server.setHandler(wcon);

        try {
            LOGGER.info(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP");
            server.start();
            System.in.read();
            LOGGER.info(">>> STOPPING EMBEDDED JETTY SERVER");
            server.stop();
            server.join();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
		}
	}

}
