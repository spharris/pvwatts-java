package io.github.spharris.pvwatts.service.weather;

import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

import com.google.common.collect.ImmutableList;

class Tmy2WeatherSource implements WeatherSource {

  private static final String TM2_DATABASE = "weather/tmy2/tmy2_data.csv";
  private static final String BASE_PATH = "target/classes/weather/tmy2/";
  
  Tmy2WeatherSource() {}
  
  Tmy2WeatherSource(String path) {
    
  }
  
  @Override
  public String getWeatherFile(Float lat, Float lon, Integer radius) {
    if (lat == null || lon == null || radius == null) {
      return null;
    }

    DataRecord closestStation = Collections.min(loadDatabase(), byDistanceFrom(lat, lon));
    double distance = Haversine.haversine(closestStation.lat, closestStation.lon, lat, lon);
    
    if (radius != 0 && distance > (double) radius) {
      return null;
    }

    return BASE_PATH + closestStation.fileName;
  }
  
  
  // TODO(spharris): Make this more robust to adding fields?
  private ImmutableList<DataRecord> loadDatabase() {
    Scanner scanner = new Scanner(getClass().getClassLoader().getResourceAsStream(TM2_DATABASE));
    
    // Skip header
    scanner.nextLine();
    
    ImmutableList.Builder<DataRecord> builder = ImmutableList.builder();
    while(scanner.hasNextLine()) {
      String[] line = scanner.nextLine().split(",");
      builder.add(new DataRecord(
        line[0],
        Float.valueOf(line[1]),
        Float.valueOf(line[2])));
    }
    
    scanner.close();
    return builder.build();
  }
  
  private Comparator<DataRecord> byDistanceFrom(final float lat, final float lon) {
    return (left, right) -> {
        double leftDist = Haversine.haversine(left.lat, left.lon, lat, lon);
        double rightDist = Haversine.haversine(right.lat, right.lon, lat, lon);
        
        return Double.compare(leftDist, rightDist);
    };
  }
  
  private static class DataRecord {
    final String fileName;
    final float lat;
    final float lon;
    
    DataRecord(String fileName, float lat, float lon) {
      this.fileName = fileName;
      this.lat = lat;
      this.lon = lon;
    }
  }
}
