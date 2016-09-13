package io.github.spharris.pvwatts.service.weather;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class IntegrationTestTmy2WeatherSource {
  
  WeatherSource tm2;
  
  @Before
  public void initializeSource() {
    tm2 = new Tmy2WeatherSource();
  }
  
  @Test
  public void returnsClosestData() {
    String expected = "target/classes/weather/tm2/26528.tm2";
    
    String result = tm2.getWeatherFile(62.30f, -150.10f, 0);
    
    assertThat(result).isEqualTo(expected);
  }
  
  @Test
  public void respectsRadiusTooBig() {
    String result = tm2.getWeatherFile(0.0f, 0.0f, 100);
    
    assertThat(result).isNull();
  }
  
  @Test
  public void radiusOfZeroIsUnlimited() {
    String expected = "target/classes/weather/tm2/11641.tm2";
    
    String result = tm2.getWeatherFile(0.0f, 0.0f, 0);
    
    assertThat(result).isEqualTo(expected);
  }
}
