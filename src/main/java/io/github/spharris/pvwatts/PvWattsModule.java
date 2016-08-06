package io.github.spharris.pvwatts;

import com.google.inject.AbstractModule;

import io.github.spharris.pvwatts.service.PvWattsServiceModule;
import io.github.spharris.pvwatts.web.PvWattsWebModule;

public class PvWattsModule extends AbstractModule {
  
  @Override
  protected void configure() {
    install(new PvWattsWebModule());
    install(new PvWattsServiceModule());
  }
}
