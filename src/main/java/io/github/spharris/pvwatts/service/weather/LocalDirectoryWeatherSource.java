package io.github.spharris.pvwatts.service.weather;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;

import com.google.common.collect.ImmutableList;

/**
 * A weather source that reads weather files from the specified directory and finds the one closest
 * to the specified latitude and longitude. 
 */
public class LocalDirectoryWeatherSource implements WeatherSource {
  
  private final Path path;
  private final WeatherSummarizer summarizer;
  
  public LocalDirectoryWeatherSource(String directory, WeatherSummarizer summarizer) {
    this.path = Paths.get(directory);
    this.summarizer = summarizer;
  }
  
  @Override
  public String getWeatherFile(Float lat, Float lon, Integer radius) {
    if (lat == null || lon == null || radius == null) {
      return null;
    }

    WeatherDataRecord closestStation = Collections.min(loadSummaryData(), byDistanceFrom(lat, lon));
    double distance = Haversine.haversine(
      closestStation.getLat(), closestStation.getLon(), lat, lon);
    
    if (radius != 0 && distance > (double) radius) {
      return null;
    }

    return path.resolve(closestStation.getFilename()).toString();
  }
  
  private ImmutableList<WeatherDataRecord> loadSummaryData() {
    
    ImmutableList.Builder<WeatherDataRecord> builder = ImmutableList.builder();
    File[] files = path.toFile().listFiles();
    for (File file : files) {
      // TODO(spharris): Recurse into subdirectories?
      if (file.isFile()) {
        try(Reader reader = new FileReader(file)) {
          builder.add(summarizer.summarizeFile(reader));
        } catch (IOException e) {
          // TODO(spharris): Anything to do here?
        }
      }
    }

    return builder.build();
  }
  
  private Comparator<WeatherDataRecord> byDistanceFrom(final float lat, final float lon) {
    return (left, right) -> {
        double leftDist = Haversine.haversine(left.getLat(), left.getLon(), lat, lon);
        double rightDist = Haversine.haversine(right.getLat(), right.getLon(), lat, lon);
        
        return Double.compare(leftDist, rightDist);
    };
  }
}
