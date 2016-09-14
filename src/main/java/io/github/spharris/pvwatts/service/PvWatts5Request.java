package io.github.spharris.pvwatts.service;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class PvWatts5Request {
  
  public static Builder builder() {
    return new AutoValue_PvWatts5Request.Builder();
  }
  
  @AutoValue.Builder
  public abstract static class Builder {
    abstract PvWatts5Request build();
  }
}
