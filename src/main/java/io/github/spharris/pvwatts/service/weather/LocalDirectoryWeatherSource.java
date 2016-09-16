package io.github.spharris.pvwatts.service.weather;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

/**
 * A weather source that reads weather files from the specified directory and finds the one closest
 * to the specified latitude and longitude. 
 */
public final class LocalDirectoryWeatherSource implements WeatherSource {

  private static final double MI_TO_KM = 5280.0 * 12.0 * 2.54 / 100.0 / 1000.0;
  
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
    
    double kmRadius = (double) radius * MI_TO_KM;
    if (radius != 0 && distance > kmRadius) {
      return null;
    }

    return path.resolve(closestStation.getFilename()).toString();
  }
  
  private ImmutableList<WeatherDataRecord> loadSummaryData() {
    File[] files = path.toFile().listFiles();
    return ImmutableList.copyOf(Arrays.stream(files).parallel()
        .filter(File::isFile)
        .map((file) -> {
          try(Reader reader = new FileReader(file)) {
            return summarizer.summarizeFile(reader);
          } catch (IOException e) {
            return null;
          }
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toList()));
  }
  
  private Comparator<WeatherDataRecord> byDistanceFrom(final float lat, final float lon) {
    return (left, right) -> {
      double leftDist = Haversine.haversine(left.getLat(), left.getLon(), lat, lon);
      double rightDist = Haversine.haversine(right.getLat(), right.getLon(), lat, lon);

      return Double.compare(leftDist, rightDist);
    };
  }
}
