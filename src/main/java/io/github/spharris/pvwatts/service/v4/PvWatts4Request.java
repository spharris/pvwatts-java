package io.github.spharris.pvwatts.service.v4;

import java.util.Map;
import java.util.Optional;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;

@AutoValue
public abstract class PvWatts4Request {

  public abstract ImmutableMap<String, String> getRawInputs();
  public abstract Optional<Float> getSystemSize();
  public abstract Optional<String> getAddress();
  public abstract Optional<Float> getLat();
  public abstract Optional<Float> getLon();
  public abstract Optional<String> getFileId();
  public abstract Optional<String> getDataset();
  public abstract Optional<Integer> getRadius();
  public abstract Optional<String> getTimeframe();
  public abstract Optional<Float> getAzimuth();
  public abstract Optional<Float> getDerate();
  public abstract Optional<Float> getTilt();
  public abstract Optional<Integer> getTiltEqLat();
  public abstract Optional<Integer> getTrackMode();
  public abstract Optional<Float> getInoct();
  public abstract Optional<Float> getGamma();
  public abstract Optional<String> getCallback();
  
  public static Builder builder() {
    return new AutoValue_PvWatts4Request.Builder();
  }
  
  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setRawInputs(Map<String, String> rawInputs);
    public abstract Builder setSystemSize(float systemSize);
    public abstract Builder setAddress(String address);
    public abstract Builder setLat(float lat);
    public abstract Builder setLon(float lon);
    public abstract Builder setFileId(String fileId);
    public abstract Builder setDataset(String dataset);
    public abstract Builder setRadius(int radius);
    public abstract Builder setTimeframe(String timeframe);
    public abstract Builder setAzimuth(float azimuth);
    public abstract Builder setDerate(float derate);
    public abstract Builder setTilt(float tilt);
    public abstract Builder setTiltEqLat(int tiltEqLat);
    public abstract Builder setTrackMode(int trackMode);
    public abstract Builder setInoct(float inoct);
    public abstract Builder setGamma(float gamma);
    public abstract Builder setCallback(String callback);
    
    public abstract PvWatts4Request build();
  }
}
