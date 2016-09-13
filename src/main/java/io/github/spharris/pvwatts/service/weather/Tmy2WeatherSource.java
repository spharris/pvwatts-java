package io.github.spharris.pvwatts.service.weather;

class Tmy2WeatherSource implements WeatherSource {

  @Override
  public String getWeatherFile(float lat, float lon, int radius) {
    return "target/classes/weather/23129.tm2";
  }
}
