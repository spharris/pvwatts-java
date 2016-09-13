package io.github.spharris.pvwatts.service.weather;

/**
 * An interface for providing the path to weather data to be used in PVWatts simulations. 
 */
public interface WeatherSource {
  
  /**
   * Fetches the path to a local weather file for the given location and radius, if available. Note
   * that it returns a <strong>relative path</strong> due to the fact that SSC does not handle
   * spaces in file paths.
   */
  String getWeatherFile(float lat, float lon, int radius);
}
