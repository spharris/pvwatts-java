package io.github.spharris.pvwatts.web;

import com.google.inject.AbstractModule;

import io.github.spharris.pvwatts.service.v4.PvWatts4Service;
import io.github.spharris.pvwatts.service.v5.PvWatts5Service;

public class PvWattsWebModule extends AbstractModule {
  
  @Override
  protected void configure() {
    requireBinding(PvWatts4Service.class);
    requireBinding(PvWatts5Service.class);
    
    bind(PvWatts4Controller.class);
    bind(PvWatts5Controller.class);
  }
}
