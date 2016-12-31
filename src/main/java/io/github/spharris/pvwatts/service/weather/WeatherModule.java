package io.github.spharris.pvwatts.service.weather;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import io.github.spharris.pvwatts.service.Annotations.WeatherDirectory;
import java.nio.file.Paths;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * Module for provided weather sources.
 */
public final class WeatherModule extends AbstractModule {

  @Override
  protected void configure() {
    MapBinder<String, WeatherSource> weatherBinder = MapBinder.newMapBinder(
      binder(), String.class, WeatherSource.class);
    
    weatherBinder.addBinding("tmy2").toProvider(new LocalDirectoryWeatherSourceProvider("tmy2",
      new Tm2FileSummarizer())).in(Singleton.class);

    weatherBinder.addBinding("tmy3").toProvider(new LocalDirectoryWeatherSourceProvider("tmy3",
      new Tmy3CsvFileSummarizer())).in(Singleton.class);
  }
  
  private static class LocalDirectoryWeatherSourceProvider
      implements Provider<LocalDirectoryWeatherSource> {
    
    @Inject @WeatherDirectory private String weatherDirectory;
    
    private final String subdirectory;
    private final WeatherSummarizer summarizer;
    
    LocalDirectoryWeatherSourceProvider(String subdirectory, WeatherSummarizer summarizer) {
      this.subdirectory = subdirectory;
      this.summarizer = summarizer;
    }
    
    public LocalDirectoryWeatherSource get() {
      return new LocalDirectoryWeatherSource(
        Paths.get(weatherDirectory, subdirectory), summarizer).initialize();
    }
  }
}
