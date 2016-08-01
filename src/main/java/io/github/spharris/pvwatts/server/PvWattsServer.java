package io.github.spharris.pvwatts.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

public class PvWattsServer {

	public static void main(String[] args) throws Exception {
		Server server = new Server(3000);
		
		ServletContextHandler handler = new ServletContextHandler(server, "/");
		
		ServletHolder sh = new ServletHolder(HttpServletDispatcher.class);
		handler.addServlet(sh, "/*");
		server.setHandler(handler);
		
		server.start();
		server.join();
	}

}
