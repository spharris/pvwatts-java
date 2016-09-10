package io.github.spharris.pvwatts.service.v4;

import javax.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

@AutoValue
public abstract class PvWatts4Response {
  
  public abstract @Nullable ImmutableMultimap<String, String> getInputs();
  public abstract @Nullable ImmutableList<String> getErrors();
  public abstract @Nullable ImmutableList<String> getWarnings();
  public abstract @Nullable String getVersion();
  public abstract @Nullable SscInfo getSscInfo();
  public abstract @Nullable StationInfo getStationInfo();
  public abstract @Nullable Outputs getOutputs();

  public static Builder builder() {
      return new AutoValue_PvWatts4Response.Builder();
  }
  
  @AutoValue.Builder
  public abstract static class Builder {
    
    public abstract Builder setInputs(Multimap<String, String> inputs);
    public abstract Builder setErrors(ImmutableList<String> errors);
    public abstract Builder setErrors(String... errors);
    public abstract Builder setWarnings(ImmutableList<String> warnings);
    public abstract Builder setWarnings(String... warnings);
    public abstract Builder setVersion(String version);
    public abstract Builder setSscInfo(SscInfo sscInfo);
    public abstract Builder setStationInfo(StationInfo stationInfo);
    public abstract Builder setOutputs(Outputs outputs);
    
    public abstract PvWatts4Response build();
  }
  
  @AutoValue
  public abstract static class SscInfo {

    public abstract @Nullable Integer getVersion();
    public abstract @Nullable String getBuild();
    
    public static Builder builder() {
      return new AutoValue_PvWatts4Response_SscInfo.Builder();
    }
    
    @AutoValue.Builder
    public abstract static class Builder {
      
      public abstract Builder setVersion(Integer version);
      public abstract Builder setBuild(String build);
      
      public abstract SscInfo build();
    }
  }
  
  @AutoValue
  public abstract static class StationInfo {
    
    public abstract @Nullable Float getLat();
    public abstract @Nullable Float getLon();
    public abstract @Nullable Float getElev();
    public abstract @Nullable Float getTz();
    public abstract @Nullable String getLocation();
    public abstract @Nullable String getCity();
    public abstract @Nullable String getState();
    public abstract @Nullable String getFileName();
    public abstract @Nullable Integer getDistance();    

    public static Builder builder() {
      return new AutoValue_PvWatts4Response_StationInfo.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
      
      public abstract Builder setLat(Float lat);
      public abstract Builder setLon(Float lon);
      public abstract Builder setElev(Float elev);
      public abstract Builder setTz(Float tz);
      public abstract Builder setLocation(String location);
      public abstract Builder setCity(String city);
      public abstract Builder setState(String state);
      public abstract Builder setFileName(String fileName);
      public abstract Builder setDistance(Integer distance);
      
      public abstract StationInfo build();
    }
  }
  
  @AutoValue
  public abstract static class Outputs {

    public abstract @Nullable ImmutableList<Float> getPoaMonthly();
    public abstract @Nullable ImmutableList<Float> getDcMonthly();
    public abstract @Nullable ImmutableList<Float> getAcMonthly();
    public abstract @Nullable Float getAcAnnual();
    public abstract @Nullable ImmutableList<Float> getSolradMonthly();
    public abstract @Nullable Float getSolradAnnual();
    public abstract @Nullable ImmutableList<Float> getAc();
    public abstract @Nullable ImmutableList<Float> getPoa();
    public abstract @Nullable ImmutableList<Float> getDn();
    public abstract @Nullable ImmutableList<Float> getDc();
    public abstract @Nullable ImmutableList<Float> getDf();
    public abstract @Nullable ImmutableList<Float> getTamb();
    public abstract @Nullable ImmutableList<Float> getTcell();
    public abstract @Nullable ImmutableList<Float> getWspd();
    
    public static Builder builder() {
      return new AutoValue_PvWatts4Response_Outputs.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {

      public abstract Builder setPoaMonthly(Iterable<Float> poaMonthly);
      public abstract Builder setDcMonthly(Iterable<Float> dcMonthly);
      public abstract Builder setAcMonthly(Iterable<Float> acMonthly);
      public abstract Builder setAcAnnual(Float acAnnual);
      public abstract Builder setSolradMonthly(Iterable<Float> solradMonthly);
      public abstract Builder setSolradAnnual(Float solradAnnual);
      public abstract Builder setAc(Iterable<Float> ac);
      public abstract Builder setPoa(Iterable<Float> poa);
      public abstract Builder setDn(Iterable<Float> dn);
      public abstract Builder setDc(Iterable<Float> dc);
      public abstract Builder setDf(Iterable<Float> df);
      public abstract Builder setTamb(Iterable<Float> tamb);
      public abstract Builder setTcell(Iterable<Float> tcell);
      public abstract Builder setWspd(Iterable<Float> wspd);
      
      public abstract Outputs build();
    }
  }
}
