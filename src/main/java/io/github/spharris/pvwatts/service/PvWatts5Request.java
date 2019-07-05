package io.github.spharris.pvwatts.service;

import com.google.auto.value.AutoValue;
import javax.annotation.Nullable;

@AutoValue
public abstract class PvWatts5Request {

  PvWatts5Request() {}

  public abstract @Nullable Float getSystemCapacity();

  public abstract @Nullable Integer getModuleType();

  public abstract @Nullable Float getLosses();

  public abstract @Nullable Integer getArrayType();

  public abstract @Nullable String getAddress();

  public abstract @Nullable Float getLat();

  public abstract @Nullable Float getLon();

  public abstract @Nullable String getFileId();

  public abstract @Nullable String getDataset();

  public abstract @Nullable Integer getRadius();

  public abstract @Nullable String getTimeframe();

  public abstract @Nullable Float getAzimuth();

  public abstract @Nullable Float getTilt();

  public abstract @Nullable Float getDcAcRatio();

  public abstract @Nullable Float getGcr();

  public abstract @Nullable Float getInvEff();

  public abstract @Nullable String getCallback();

  public abstract Builder toBuilder();

  public static Builder builder() {
    return new AutoValue_PvWatts5Request.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setSystemCapacity(@Nullable Float systemCapacity);

    public abstract Builder setModuleType(@Nullable Integer moduleType);

    public abstract Builder setLosses(@Nullable Float losses);

    public abstract Builder setArrayType(@Nullable Integer arrayType);

    public abstract Builder setAddress(@Nullable String address);

    public abstract Builder setLat(@Nullable Float lat);

    public abstract Builder setLon(@Nullable Float lon);

    public abstract Builder setFileId(@Nullable String fileId);

    public abstract Builder setDataset(@Nullable String dataset);

    public abstract Builder setRadius(@Nullable Integer radius);

    public abstract Builder setTimeframe(@Nullable String timeframe);

    public abstract Builder setAzimuth(@Nullable Float azimuth);

    public abstract Builder setTilt(@Nullable Float tilt);

    public abstract Builder setDcAcRatio(@Nullable Float dcAcRatio);

    public abstract Builder setGcr(@Nullable Float gcr);

    public abstract Builder setInvEff(@Nullable Float invEff);

    public abstract Builder setCallback(@Nullable String callback);

    public abstract PvWatts5Request build();
  }
}
