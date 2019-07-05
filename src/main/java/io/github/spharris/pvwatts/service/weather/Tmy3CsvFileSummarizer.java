package io.github.spharris.pvwatts.service.weather;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/** Summarizes TMY3-style CSV files */
public final class Tmy3CsvFileSummarizer implements WeatherSummarizer {

  /** Reads a the header from a TMY3-style CSV file and summarizes it. */
  @Override
  public WeatherDataRecord summarizeFile(Reader file) throws IOException {
    Iterator<CSVRecord> records = CSVFormat.DEFAULT.parse(file).iterator();
    CSVRecord header = records.next();

    return WeatherDataRecord.builder()
        .setFilename(header.get(0) + "TYA.csv")
        .setLat(Float.valueOf(header.get(4)))
        .setLon(Float.valueOf(header.get(5)))
        .build();
  }
}
