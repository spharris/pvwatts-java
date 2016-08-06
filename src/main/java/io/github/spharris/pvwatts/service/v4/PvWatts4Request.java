package io.github.spharris.pvwatts.service.v4;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

public final class PvWatts4Request {

  private final ImmutableMap<String, String> rawInputs;
  
  private final Optional<Float> systemSize;
  
  private final Optional<String> address;
  private final Optional<Float> lat;
  private final Optional<Float> lon;
  
  private final Optional<String> fileId;
  private final Optional<String> dataset;
  private final Optional<Integer> radius;
  
  private final Optional<String> timeframe;
  
  private final Optional<Float> azimuth;
  private final Optional<Float> derate;
  private final Optional<Float> tilt;
  private final Optional<Integer> tiltEqLat;
  private final Optional<Integer> trackMode;
  private final Optional<Float> inoct;
  private final Optional<Float> gamma;
  
  private final Optional<String> callback;
  
  private PvWatts4Request(ImmutableMap<String, String> rawInputs,
                          Optional<Float> systemSize,
                          Optional<String> address,
                          Optional<Float> lat,
                          Optional<Float> lon,
                          Optional<String> fileId,
                          Optional<String> dataset,
                          Optional<Integer> radius,
                          Optional<String> timeframe,
                          Optional<Float> azimuth,
                          Optional<Float> derate,
                          Optional<Float> tilt,
                          Optional<Integer> tiltEqLat,
                          Optional<Integer> trackMode,
                          Optional<Float> inoct,
                          Optional<Float> gamma,
                          Optional<String> callback) {
    this.rawInputs = rawInputs;
    this.systemSize = systemSize;
    this.address = address;
    this.lat = lat;
    this.lon = lon;
    this.fileId = fileId;
    this.dataset = dataset;
    this.radius = radius;
    this.timeframe = timeframe;
    this.azimuth = azimuth;
    this.derate = derate;
    this.tilt = tilt;
    this.tiltEqLat = tiltEqLat;
    this.trackMode = trackMode;
    this.inoct = inoct;
    this.gamma = gamma;
    this.callback = callback;
  }
  
  public static Builder newBuilder() {
    return new Builder();
  }
  
  public static class Builder {
    private Optional<Float> systemSize = Optional.empty();
    private Optional<String> address = Optional.empty();
    private Optional<Float> lat = Optional.empty();
    private Optional<Float> lon = Optional.empty();
    private Optional<String> fileId = Optional.empty();
    private Optional<String> dataset = Optional.empty();
    private Optional<Integer> radius = Optional.empty();
    private Optional<String> timeframe = Optional.empty();
    private Optional<Float> azimuth = Optional.empty();
    private Optional<Float> derate = Optional.empty();
    private Optional<Float> tilt = Optional.empty();
    private Optional<Integer> tiltEqLat = Optional.empty();
    private Optional<Integer> trackMode = Optional.empty();
    private Optional<Float> inoct = Optional.empty();
    private Optional<Float> gamma = Optional.empty();
    private Optional<String> callback = Optional.empty();
    private ImmutableMap<String, String> rawInputs = ImmutableMap.of();
    
    public PvWatts4Request build() {
      return new PvWatts4Request(rawInputs,
        systemSize,
        address,
        lat,
        lon,
        fileId,
        dataset,
        radius,
        timeframe,
        azimuth,
        derate,
        tilt,
        tiltEqLat,
        trackMode,
        inoct,
        gamma,
        callback);
    }
    
    public Builder setRawInputs(Map<String, String> rawInputs) {
      rawInputs = ImmutableMap.copyOf(rawInputs);
      return this;
    }
    
    public Builder setSystemSize(Float systemSize) {
      this.systemSize = Optional.ofNullable(systemSize);
      return this;
    }

    public Builder setAddress(String address) {
      this.address = Optional.ofNullable(address);
      return this;
    }

    public Builder setLat(Float lat) {
      this.lat = Optional.ofNullable(lat);
      return this;
    }

    public Builder setLon(Float lon) {
      this.lon = Optional.ofNullable(lon);
      return this;
    }

    public Builder setFileId(String fileId) {
      this.fileId = Optional.ofNullable(fileId);
      return this;
    }

    public Builder setDataset(String dataset) {
      this.dataset = Optional.ofNullable(dataset);
      return this;
    }

    public Builder setRadius(Integer radius) {
      this.radius = Optional.ofNullable(radius);
      return this;
    }

    public Builder setTimeframe(String timeframe) {
      this.timeframe = Optional.ofNullable(timeframe);
      return this;
    }

    public Builder setAzimuth(Float azimuth) {
      this.azimuth = Optional.ofNullable(azimuth);
      return this;
    }

    public Builder setDerate(Float derate) {
      this.derate = Optional.ofNullable(derate);
      return this;
    }

    public Builder setTilt(Float tilt) {
      this.tilt = Optional.ofNullable(tilt);
      return this;
    }

    public Builder setTiltEqLat(Integer tilt_eq_lat) {
      this.tiltEqLat = Optional.ofNullable(tilt_eq_lat);
      return this;
    }

    public Builder setTrackMode(Integer track_mode) {
      this.trackMode = Optional.ofNullable(track_mode);
      return this;
    }

    public Builder setInoct(Float inoct) {
      this.inoct = Optional.ofNullable(inoct);
      return this;
    }

    public Builder setGamma(Float gamma) {
      this.gamma = Optional.ofNullable(gamma);
      return this;
    }

    public Builder setCallback(String callback) {
      this.callback = Optional.ofNullable(callback);
      return this;
    }
  }

  public ImmutableMap<String, String> getRawInputs() {
    return rawInputs;
  }
  
  public Optional<Float> getSystemSize() {
    return systemSize;
  }

  public Optional<String> getAddress() {
    return address;
  }

  public Optional<Float> getLat() {
    return lat;
  }

  public Optional<Float> getLon() {
    return lon;
  }

  public Optional<String> getFileId() {
    return fileId;
  }

  public Optional<String> getDataset() {
    return dataset;
  }

  public Optional<Integer> getRadius() {
    return radius;
  }

  public Optional<String> getTimeframe() {
    return timeframe;
  }

  public Optional<Float> getAzimuth() {
    return azimuth;
  }

  public Optional<Float> getDerate() {
    return derate;
  }

  public Optional<Float> getTilt() {
    return tilt;
  }

  public Optional<Integer> getTiltEqLat() {
    return tiltEqLat;
  }

  public Optional<Integer> getTrackMode() {
    return trackMode;
  }

  public Optional<Float> getInoct() {
    return inoct;
  }

  public Optional<Float> getGamma() {
    return gamma;
  }

  public Optional<String> getCallback() {
    return callback;
  }
}
