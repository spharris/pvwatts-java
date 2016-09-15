package io.github.spharris.pvwatts.service.weather;

import java.io.IOException;
import java.io.Reader;

public interface WeatherSummarizer {
  WeatherDataRecord summarizeFile(Reader file) throws IOException;
}
