package io.github.spharris.pvwatts.service.weather;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.multibindings.MapBinder;
import io.github.spharris.pvwatts.service.Annotations.WeatherDirectory;
import java.nio.file.Paths;
import javax.inject.Provider;

/**
 * Module for provided weather sources.
 */
public final class WeatherModule extends AbstractModule {

  @Override
  protected void configure() {
    MapBinder<String, WeatherSource> weatherBinder = MapBinder.newMapBinder(
      binder(), String.class, WeatherSource.class);
    
    weatherBinder.addBinding("tmy2").toProvider(getLocalSource("tmy2",
      getProvider(Key.get(String.class, WeatherDirectory.class)), new Tm2FileSummarizer()));
      

    weatherBinder.addBinding("tmy3").toProvider(getLocalSource("tmy3",
      getProvider(Key.get(String.class, WeatherDirectory.class)), new Tmy3CsvFileSummarizer()));
      
  }
  
  private static Provider<LocalDirectoryWeatherSource> getLocalSource(String subDirectory,
      Provider<String> weatherDirectoryProvider, WeatherSummarizer summarizer) {
    return () -> new LocalDirectoryWeatherSource(
      Paths.get(weatherDirectoryProvider.get(), subDirectory), summarizer);
  }
}
