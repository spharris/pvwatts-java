package io.github.spharris.pvwatts.service.weather;

import java.io.IOException;
import java.io.Reader;

/** An interface for summarizing weather data, usually from a local file. */
public interface WeatherSummarizer {
  WeatherDataRecord summarizeFile(Reader file) throws IOException;
}
