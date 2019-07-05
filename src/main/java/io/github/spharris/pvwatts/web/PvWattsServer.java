package io.github.spharris.pvwatts.web;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.fasterxml.jackson.jaxrs.xml.JacksonXMLProvider;
import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import io.github.spharris.pvwatts.PvWattsModule;
import io.github.spharris.pvwatts.web.Annotations.Port;
import org.apache.commons.cli.ParseException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

public final class PvWattsServer {

  public static void main(String[] args) throws Exception {
    Injector injector = Guice.createInjector(getModules(args));

    Server server = new Server(injector.getInstance(Key.get(Integer.class, Port.class)));
    ServletContextHandler handler = new ServletContextHandler(server, "/pvwatts");

    handler.addEventListener(
        injector.getInstance(GuiceResteasyBootstrapServletContextListener.class));

    ServletHolder sh = new ServletHolder(HttpServletDispatcher.class);
    handler.addServlet(sh, "/*");
    server.setHandler(handler);

    server.start();
    server.join();
  }

  private static ImmutableList<Module> getModules(String[] args) throws ParseException {
    return ImmutableList.<Module>of(
        new PvWattsModule(),
        new PvWattsFlagsModule(args),
        new AbstractModule() {
          @Override
          protected void configure() {
            bind(JacksonJsonProvider.class);
            bind(JacksonXMLProvider.class);
          }
        });
  }
}
