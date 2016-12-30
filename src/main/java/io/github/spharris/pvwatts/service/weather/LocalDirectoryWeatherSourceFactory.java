package io.github.spharris.pvwatts.service.weather;

public interface LocalDirectoryWeatherSourceFactory {
  public LocalDirectoryWeatherSource create(String subDirectory, WeatherSummarizer summarizer);
}
