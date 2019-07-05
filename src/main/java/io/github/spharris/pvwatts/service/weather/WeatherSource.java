package io.github.spharris.pvwatts.service.weather;

/** An interface for providing the path to weather data to be used in PVWatts simulations. */
public interface WeatherSource {

  /**
   * Fetches the path to a local weather file for the given location and radius, if available. Note
   * that paths must not contain spaces, because the SSC library does not handle them gracefully.
   *
   * @param lat The latitude.
   * @param lon The longitude.
   * @param radius The search radius (in mi)
   * @return The location of the weather file to use, if available. <code>null</code> otherwise.
   */
  String getWeatherFile(Float lat, Float lon, Integer radius);
}
