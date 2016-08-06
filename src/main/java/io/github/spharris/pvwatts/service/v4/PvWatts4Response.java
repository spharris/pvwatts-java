package io.github.spharris.pvwatts.service.v4;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@AutoValue
public abstract class PvWatts4Response {
  
  public abstract ImmutableMap<String, String> getInputs();
  public abstract ImmutableList<String> getErrors();
  public abstract ImmutableList<String> getWarnings();
  public abstract String getVersion();
  public abstract SscInfo getSscInfo();
  public abstract StationInfo getStationInfo();
  public abstract Outputs getOutputs();

  public static Builder builder() {
      return new AutoValue_PvWatts4Response.Builder();
  }
  
  @AutoValue.Builder
  public abstract static class Builder {
    
    public abstract Builder setInputs(ImmutableMap<String, String> inputs);
    public abstract Builder setErrors(ImmutableList<String> errors);
    public abstract Builder setWarnings(ImmutableList<String> warnings);
    public abstract Builder setVersion(String version);
    public abstract Builder setSscInfo(SscInfo sscInfo);
    public abstract Builder setStationInfo(StationInfo stationInfo);
    public abstract Builder setOutputs(Outputs outputs);
    
    public abstract PvWatts4Response build();
  }
  
  @AutoValue
  public abstract static class SscInfo {

    public abstract int getVersion();
    public abstract String getBuild();
    
    public static Builder builder() {
      return new AutoValue_PvWatts4Response_SscInfo.Builder();
    }
    
    @AutoValue.Builder
    public abstract static class Builder {
      
      public abstract Builder setVersion(int version);
      public abstract Builder setBuild(String build);
      
      public abstract SscInfo build();
    }
  }
  
  @AutoValue
  public abstract static class StationInfo {
    
    public abstract float getLat();
    public abstract float getLon();
    public abstract float getElev();
    public abstract float getTz();
    public abstract String getLocation();
    public abstract String getCity();
    public abstract String getState();
    public abstract String getFileName();
    public abstract int getDistance();    

    public static Builder builder() {
      return new AutoValue_PvWatts4Response_StationInfo.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
      
      public abstract Builder setLat(float lat);
      public abstract Builder setLon(float lon);
      public abstract Builder setElev(float elev);
      public abstract Builder setTz(float tz);
      public abstract Builder setLocation(String location);
      public abstract Builder setCity(String city);
      public abstract Builder setState(String state);
      public abstract Builder setFileName(String fileName);
      public abstract Builder setDistance(int distance);
      
      public abstract StationInfo build();
    }
  }
  
  @AutoValue
  public abstract static class Outputs {

    public abstract ImmutableList<Float> getPoaMonthly();
    public abstract ImmutableList<Float> getDcMonthly();
    public abstract ImmutableList<Float> getAcMonthly();
    public abstract float getAcAnnual();
    public abstract ImmutableList<Float> getSolradMonthly();
    public abstract float getSolradAnnual();
    public abstract ImmutableList<Float> getAc();
    public abstract ImmutableList<Float> getPoa();
    public abstract ImmutableList<Float> getDn();
    public abstract ImmutableList<Float> getDc();
    public abstract ImmutableList<Float> getDf();
    public abstract ImmutableList<Float> getTamb();
    public abstract ImmutableList<Float> getTcell();
    public abstract ImmutableList<Float> getWspd();
    
    public static Builder builder() {
      return new AutoValue_PvWatts4Response_Outputs.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {

      public abstract Builder setPoaMonthly(ImmutableList<Float> poaMonthly);
      public abstract Builder setDcMonthly(ImmutableList<Float> dcMonthly);
      public abstract Builder setAcMonthly(ImmutableList<Float> acMonthly);
      public abstract Builder setAcAnnual(float acAnnual);
      public abstract Builder setSolradMonthly(ImmutableList<Float> solradMonthly);
      public abstract Builder setSolradAnnual(float solradAnnual);
      public abstract Builder setAc(ImmutableList<Float> ac);
      public abstract Builder setPoa(ImmutableList<Float> poa);
      public abstract Builder setDn(ImmutableList<Float> dn);
      public abstract Builder setDc(ImmutableList<Float> dc);
      public abstract Builder setDf(ImmutableList<Float> df);
      public abstract Builder setTamb(ImmutableList<Float> tamb);
      public abstract Builder setTcell(ImmutableList<Float> tcell);
      public abstract Builder setWspd(ImmutableList<Float> wspd);
      
      public abstract Outputs build();
    }
  }
}
