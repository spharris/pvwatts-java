package io.github.spharris.pvwatts.web;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

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
      new PvWattsWebModule());
  }

}
