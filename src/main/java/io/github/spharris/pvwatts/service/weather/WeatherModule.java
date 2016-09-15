package io.github.spharris.pvwatts.service.weather;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

import io.github.spharris.pvwatts.Configuration;

public class WeatherModule extends AbstractModule {

  @Override
  protected void configure() {
    MapBinder<String, WeatherSource> weatherBinder = MapBinder.newMapBinder(
      binder(),
      String.class,
      WeatherSource.class);
    
    weatherBinder.addBinding("tmy2").toInstance(
      new LocalDirectoryWeatherSource(
        Configuration.weatherDirectory().resolve("tmy2").toAbsolutePath().toString(),
        new Tm2FileSummarizer()));
  }
}
