package io.github.spharris.pvwatts;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Configuration {
  
  private static final Configuration INSTANCE = new Configuration();
  
  private String weatherDirectory;
  
  private Configuration() {}
  
  public static void loadConfig(InputStream config) {
    Properties properties = new Properties();
    try {
      properties.load(config);
    } catch(IOException e) {
      throw new IllegalArgumentException("Could not load configuration properties.", e);
    }
    
    INSTANCE.weatherDirectory = properties.getProperty("pvwatts.weatherDirectory", "weather/");
  }
  
  public static String weatherDirectory() {
    return INSTANCE.weatherDirectory;
  }
}
