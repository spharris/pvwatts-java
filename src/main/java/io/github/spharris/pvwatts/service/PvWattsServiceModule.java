package io.github.spharris.pvwatts.service;

import com.google.inject.AbstractModule;

import io.github.spharris.pvwatts.service.weather.WeatherModule;
import io.github.spharris.ssc.ModuleFactory;

public final class PvWattsServiceModule extends AbstractModule {

  @Override
  protected void configure() {
    requireBinding(ModuleFactory.class);
    
    install(new WeatherModule());
    
    bind(PvWatts4Service.class);
    bind(PvWatts5Service.class);
  }

}