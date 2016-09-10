package io.github.spharris.pvwatts.service.v4;

import javax.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class PvWatts4Request {

  public abstract @Nullable Float getSystemSize();
  public abstract @Nullable String getAddress();
  public abstract @Nullable Float getLat();
  public abstract @Nullable Float getLon();
  public abstract @Nullable String getFileId();
  public abstract @Nullable String getDataset();
  public abstract @Nullable Float getRadius();
  public abstract @Nullable String getTimeframe();
  public abstract @Nullable Float getAzimuth();
  public abstract @Nullable Float getDerate();
  public abstract @Nullable Float getTilt();
  public abstract @Nullable Float getTiltEqLat();
  public abstract @Nullable Float getTrackMode();
  public abstract @Nullable Float getInoct();
  public abstract @Nullable Float getGamma();
  public abstract @Nullable String getCallback();
  
  public static Builder builder() {
    return new AutoValue_PvWatts4Request.Builder();
  }
  
  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setSystemSize(@Nullable Float systemSize);
    public abstract Builder setAddress(@Nullable String address);
    public abstract Builder setLat(@Nullable Float lat);
    public abstract Builder setLon(@Nullable Float lon);
    public abstract Builder setFileId(@Nullable String fileId);
    public abstract Builder setDataset(@Nullable String dataset);
    public abstract Builder setRadius(@Nullable Float radius);
    public abstract Builder setTimeframe(@Nullable String timeframe);
    public abstract Builder setAzimuth(@Nullable Float azimuth);
    public abstract Builder setDerate(@Nullable Float derate);
    public abstract Builder setTilt(@Nullable Float tilt);
    public abstract Builder setTiltEqLat(@Nullable Float tiltEqLat);
    public abstract Builder setTrackMode(@Nullable Float trackMode);
    public abstract Builder setInoct(@Nullable Float inoct);
    public abstract Builder setGamma(@Nullable Float gamma);
    public abstract Builder setCallback(@Nullable String callback);
    
    public abstract PvWatts4Request build();
  }
}
