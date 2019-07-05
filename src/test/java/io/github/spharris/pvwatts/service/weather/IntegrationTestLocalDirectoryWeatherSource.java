package io.github.spharris.pvwatts.service.weather;

import static com.google.common.truth.Truth.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class IntegrationTestLocalDirectoryWeatherSource {

  private Path basePath;
  private WeatherSource tm2;

  @Before
  public void initializeSource() throws Exception {
    basePath = Paths.get(getClass().getClassLoader().getResource("weather/tmy2/").toURI());
    tm2 = new LocalDirectoryWeatherSource(basePath, new Tm2FileSummarizer()).initialize();
  }

  @Test
  public void returnsClosestData() {
    String expected = basePath.resolve("23129.tm2").toString();

    String result = tm2.getWeatherFile(33.816f, -118.15f, 25);

    assertThat(result).isEqualTo(expected);
  }

  @Test
  public void respectsRadiusTooBig() {
    String result = tm2.getWeatherFile(0.0f, 0.0f, 100);

    assertThat(result).isNull();
  }

  @Test
  public void radiusOfZeroIsUnlimited() {
    String expected = basePath.resolve("11641.tm2").toString();

    String result = tm2.getWeatherFile(0.0f, 0.0f, 0);

    assertThat(result).isEqualTo(expected);
  }
}
