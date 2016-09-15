package io.github.spharris.pvwatts.service.weather;

import static com.google.common.truth.Truth.assertThat;

import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

public class UnitTestTmy3CsvFileSummarizer {
  
  static final Reader DATA =
      new StringReader("690150,\"TWENTYNINE PALMS\",CA,-8.0,34.300,-116.167,626");
  
  WeatherSummarizer summarizer = new Tmy3CsvFileSummarizer();
  
  @Test
  public void readsDataCorrectly() throws Exception {
    WeatherDataRecord result = summarizer.summarizeFile(DATA);
    
    WeatherDataRecord expected = WeatherDataRecord.builder()
        .setFilename("690150TYA.csv")
        .setLat(34.300f)
        .setLon(-116.167f)
        .build();
    
    assertThat(result).isEqualTo(expected);
  }
}
