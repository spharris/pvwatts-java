package io.github.spharris.pvwatts;

import static com.google.common.truth.Truth.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class UnitTestConfiguration {

  static final InputStream CONFIG_DATA = new ByteArrayInputStream(new StringBuffer()
    .append("pvwatts.weatherDirectory=weatherDir/\n")
    .toString().getBytes());
  
  @Test
  public void loadsConfiguration() {
    Configuration.loadConfig(CONFIG_DATA);
    assertThat(Configuration.weatherDirectory().toString())
        .isEqualTo(Paths.get("weatherDir/").toString());
  }
  
  @Test
  public void loadsDefaults() {
    Configuration.loadConfig(new ByteArrayInputStream(new byte[0]));
    assertThat(Configuration.weatherDirectory().toString())
        .isEqualTo(Paths.get("weather/").toString());
  }
}
