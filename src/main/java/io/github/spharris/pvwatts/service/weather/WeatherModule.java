package io.github.spharris.pvwatts.service.weather;

import com.google.inject.AbstractModule;

import io.github.spharris.pvwatts.service.weather.Annotations.Tmy2;

public class WeatherModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(WeatherSource.class).annotatedWith(Tmy2.class).to(Tmy2WeatherSource.class);
  }
}
