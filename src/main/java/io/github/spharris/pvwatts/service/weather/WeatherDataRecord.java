package io.github.spharris.pvwatts.service.weather;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class WeatherDataRecord {
  
  WeatherDataRecord() {}
  
  public abstract String getFilename();
  public abstract float getLat();
  public abstract float getLon();
  
  public static Builder builder() {
    return new AutoValue_WeatherDataRecord.Builder();
  }
  
  @AutoValue.Builder
  public abstract static class Builder {
    
    public abstract Builder setFilename(String fileName);
    public abstract Builder setLat(float lat);
    public abstract Builder setLon(float lon);
    
    public abstract WeatherDataRecord build();
  }
}
