package io.github.spharris.pvwatts.web;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.fasterxml.jackson.jaxrs.xml.JacksonXMLProvider;
import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import io.github.spharris.pvwatts.PvWattsModule;

public class PvWattsServer {

  public static void main(String[] args) throws Exception {
    Server server = new Server(3000);

    Injector injector = Guice.createInjector(getModules());
    ServletContextHandler handler = new ServletContextHandler(server, "/pvwatts");
    handler.addEventListener(injector.getInstance(
      GuiceResteasyBootstrapServletContextListener.class));

    ServletHolder sh = new ServletHolder(HttpServletDispatcher.class);
    handler.addServlet(sh, "/*");
    server.setHandler(handler);

    server.start();
    server.join();
  }
  
  private static ImmutableList<Module> getModules() {
    return ImmutableList.<Module>of(
      new PvWattsModule(),
      new AbstractModule() {
        @Override
        protected void configure() {
          bind(JacksonJsonProvider.class);
          bind(JacksonXMLProvider.class);
        }
      });
  }
}
