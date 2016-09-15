package io.github.spharris.pvwatts;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class Configuration {
  
  private static final Configuration INSTANCE = new Configuration();
  
  private Path weatherDirectory;
  
  private Configuration() {}
  
  public static void loadConfig(InputStream config) {
    Properties properties = new Properties();
    try {
      properties.load(config);
    } catch(IOException e) {
      throw new IllegalArgumentException("Could not load configuration properties.", e);
    }
    
    INSTANCE.weatherDirectory = Paths.get(
      properties.getProperty("pvwatts.weatherDirectory", "weather/"));
  }
  
  public static Path weatherDirectory() {
    return INSTANCE.weatherDirectory;
  }
}
