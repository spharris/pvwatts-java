package io.github.spharris.pvwatts;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.fasterxml.jackson.jaxrs.xml.JacksonXMLProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import io.github.spharris.ssc.web.ModuleService;
import io.github.spharris.ssc.web.handlers.UncaughtExceptionHandler;
import io.github.spharris.ssc.web.handlers.UnknownModuleHandler;

public class PVWattsJava {

	public static void main(String[] args) throws Exception {
		Injector injector = Guice.createInjector(new SscWebModule());
		
		Server server = new Server(3000);
		
		ServletContextHandler handler = new ServletContextHandler(server, "/");
		handler.addEventListener(injector.getInstance(GuiceResteasyBootstrapServletContextListener.class));
		
		ServletHolder sh = new ServletHolder(HttpServletDispatcher.class);
		handler.addServlet(sh, "/*");
		server.setHandler(handler);
		
		server.start();
		server.join();
	}
	
	private static class SscWebModule extends AbstractModule {
		
		@Override
		protected void configure() {
			bind(ModuleService.class);

			// Exception handlers
			bind(UncaughtExceptionHandler.class);
			bind(UnknownModuleHandler.class);
			
			// Serialization providers
			bind(JacksonJsonProvider.class);
			bind(JacksonXMLProvider.class);
		}
		
	}
}
