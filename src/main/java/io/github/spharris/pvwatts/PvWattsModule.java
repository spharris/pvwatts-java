package io.github.spharris.pvwatts;

import com.google.inject.AbstractModule;
import io.github.spharris.pvwatts.service.PvWattsServiceModule;
import io.github.spharris.pvwatts.web.PvWattsWebModule;
import io.github.spharris.ssc.SscGuiceModule;

public final class PvWattsModule extends AbstractModule {

  @Override
  protected void configure() {
    install(new SscGuiceModule());
    install(new PvWattsWebModule());
    install(new PvWattsServiceModule());
  }
}
