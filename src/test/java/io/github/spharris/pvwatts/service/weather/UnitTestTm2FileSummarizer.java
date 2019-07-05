package io.github.spharris.pvwatts.service.weather;

import static com.google.common.truth.Truth.assertThat;

import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class UnitTestTm2FileSummarizer {

  static final Reader DATA =
      new StringReader(" 11641 SAN_JUAN               PR  -4 N 18 26 W  66  0    19");

  WeatherSummarizer summarizer = new Tm2FileSummarizer();

  @Test
  public void readsDataCorrectly() throws Exception {
    WeatherDataRecord result = summarizer.summarizeFile(DATA);

    WeatherDataRecord expected =
        WeatherDataRecord.builder()
            .setFilename("11641.tm2")
            .setLat((float) (18.0 + 26.0 / 60.0))
            .setLon(-66f)
            .build();

    assertThat(result).isEqualTo(expected);
  }
}
