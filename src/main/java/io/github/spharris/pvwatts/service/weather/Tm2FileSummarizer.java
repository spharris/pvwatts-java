package io.github.spharris.pvwatts.service.weather;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.io.Reader;

/** Summarize a .tm2 file. */
public final class Tm2FileSummarizer implements WeatherSummarizer {

  /** Reads the header from a TM2 file and returns a {@link WeatherDataRecord} summarizing it. */
  @Override
  public WeatherDataRecord summarizeFile(Reader file) throws IOException {
    WeatherDataRecord.Builder builder = WeatherDataRecord.builder();

    // http://rredc.nrel.gov/solar/pubs/tmy2/tab3-1.html
    char[] header = new char[59];
    int read = file.read(header);
    checkState(
        read == header.length,
        "Read an incorrect number of bytes from TM2 header. Got %d expected %d.",
        read,
        header.length);

    builder
        .setFilename(new String(header, 1, 5).trim() + ".tm2")
        .setLat(
            getCoord(
                new String(header, 37, 1),
                Integer.parseInt(new String(header, 39, 2).trim()),
                Integer.parseInt(new String(header, 42, 2).trim())))
        .setLon(
            getCoord(
                new String(header, 45, 1),
                Integer.parseInt(new String(header, 47, 3).trim()),
                Integer.parseInt(new String(header, 51, 2).trim())));

    return builder.build();
  }

  private static float getCoord(String direction, int degrees, int minutes) {
    float coord = degrees + minutes / 60f;
    if (direction.equals("W") || direction.equals("S")) {
      return coord * -1.0f;
    }

    return coord;
  }
}
