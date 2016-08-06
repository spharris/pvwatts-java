package io.github.spharris.pvwatts.service;

import com.google.inject.AbstractModule;

import io.github.spharris.ssc.Module;

public class PvWattsServiceModule extends AbstractModule {

  @Override
  protected void configure() {
    requireBinding(Module.class);
    
    bind(PvWatts4Service.class);
    bind(PvWatts5Service.class);
  }

}
