package io.github.spharris.pvwatts.service;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import io.github.spharris.pvwatts.utils.ImmutableMultimapSerializer;

@AutoValue
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = AutoValue_PvWatts4Response.Builder.class)
public abstract class PvWatts4Response {
  
  PvWatts4Response() {}
  
  @JsonSerialize(using = ImmutableMultimapSerializer.class)
  public abstract @Nullable ImmutableMultimap<String, String> getInputs();

  public abstract @Nullable ImmutableList<String> getErrors();
  public abstract @Nullable ImmutableList<String> getWarnings();
  public abstract @Nullable String getVersion();
  public abstract @Nullable Outputs getOutputs();
  
  @JsonProperty("ssc_info") public abstract @Nullable SscInfo getSscInfo();
  @JsonProperty("station_info") public abstract @Nullable StationInfo getStationInfo();

  public static Builder builder() {
      return new AutoValue_PvWatts4Response.Builder();
  }
  
  @AutoValue.Builder
  @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "set")
  public abstract static class Builder {

    public abstract Builder setErrors(String... errors);
    public abstract Builder setWarnings(String... warnings);
    public abstract Builder setVersion(String version);
    public abstract Builder setOutputs(Outputs outputs);
    
    @JsonProperty("errors") public abstract Builder setErrors(ImmutableList<String> errors);
    @JsonProperty("warnings") public abstract Builder setWarnings(ImmutableList<String> warnings);
    @JsonProperty("ssc_info") public abstract Builder setSscInfo(SscInfo sscInfo);
    @JsonProperty("station_info") public abstract Builder setStationInfo(StationInfo stationInfo);
    
    @JsonIgnore public abstract Builder setInputs(Multimap<String, String> inputs);
    
    public abstract PvWatts4Response build();
  }
  
  @AutoValue
  @JsonInclude(Include.NON_NULL)
  @JsonDeserialize(builder = AutoValue_PvWatts4Response_SscInfo.Builder.class)
  public abstract static class SscInfo {

    public abstract @Nullable Integer getVersion();
    public abstract @Nullable String getBuild();
    
    public static Builder builder() {
      return new AutoValue_PvWatts4Response_SscInfo.Builder();
    }
    
    @AutoValue.Builder
    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "set")
    public abstract static class Builder {
      
      public abstract Builder setVersion(Integer version);
      public abstract Builder setBuild(String build);
      
      public abstract SscInfo build();
    }
  }
  
  @AutoValue
  @JsonInclude(Include.NON_NULL)
  @JsonDeserialize(builder = AutoValue_PvWatts4Response_StationInfo.Builder.class)
  public abstract static class StationInfo {
    
    public abstract @Nullable Float getLat();
    public abstract @Nullable Float getLon();
    public abstract @Nullable Float getElev();
    public abstract @Nullable Float getTz();
    public abstract @Nullable String getLocation();
    public abstract @Nullable String getCity();
    public abstract @Nullable String getState();
    public abstract @Nullable Integer getDistance();
    
    @JsonProperty("file_name") public abstract @Nullable String getFileName();

    public static Builder builder() {
      return new AutoValue_PvWatts4Response_StationInfo.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "set")
    public abstract static class Builder {

      public abstract Builder setLat(Float lat);
      public abstract Builder setLon(Float lon);
      public abstract Builder setElev(Float elev);
      public abstract Builder setTz(Float tz);
      public abstract Builder setLocation(String location);
      public abstract Builder setCity(String city);
      public abstract Builder setState(String state);
      public abstract Builder setDistance(Integer distance);
      
      @JsonProperty("file_name") public abstract Builder setFileName(String fileName);
      
      public abstract StationInfo build();
    }
  }
  
  @AutoValue
  @JsonInclude(Include.NON_NULL)
  @JsonDeserialize(builder = AutoValue_PvWatts4Response_Outputs.Builder.class)
  public abstract static class Outputs {

    public abstract @Nullable ImmutableList<Float> getAc();
    public abstract @Nullable ImmutableList<Float> getPoa();
    public abstract @Nullable ImmutableList<Float> getDn();
    public abstract @Nullable ImmutableList<Float> getDc();
    public abstract @Nullable ImmutableList<Float> getDf();
    public abstract @Nullable ImmutableList<Float> getTamb();
    public abstract @Nullable ImmutableList<Float> getTcell();
    public abstract @Nullable ImmutableList<Float> getWspd();
    
    @JsonProperty("poa_monthly") public abstract @Nullable ImmutableList<Float> getPoaMonthly();
    @JsonProperty("dc_monthly") public abstract @Nullable ImmutableList<Float> getDcMonthly();
    @JsonProperty("ac_monthly") public abstract @Nullable ImmutableList<Float> getAcMonthly();
    @JsonProperty("ac_annual") public abstract @Nullable Float getAcAnnual();
    @JsonProperty("solrad_annual") public abstract @Nullable Float getSolradAnnual();
    
    @JsonProperty("solrad_monthly")
    public abstract @Nullable ImmutableList<Float> getSolradMonthly();
    
    public static Builder builder() {
      return new AutoValue_PvWatts4Response_Outputs.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "set")
    public abstract static class Builder {

      public abstract Builder setAc(Iterable<Float> ac);
      public abstract Builder setPoa(Iterable<Float> poa);
      public abstract Builder setDn(Iterable<Float> dn);
      public abstract Builder setDc(Iterable<Float> dc);
      public abstract Builder setDf(Iterable<Float> df);
      public abstract Builder setTamb(Iterable<Float> tamb);
      public abstract Builder setTcell(Iterable<Float> tcell);
      public abstract Builder setWspd(Iterable<Float> wspd);
      
      @JsonProperty("poa_monthly")
      public abstract Builder setPoaMonthly(Iterable<Float> poaMonthly);
      
      @JsonProperty("solrad_monthly")
      public abstract Builder setSolradMonthly(Iterable<Float> solradMonthly);
      
      @JsonProperty("dc_monthly") public abstract Builder setDcMonthly(Iterable<Float> dcMonthly);
      @JsonProperty("ac_monthly") public abstract Builder setAcMonthly(Iterable<Float> acMonthly);
      @JsonProperty("ac_annual") public abstract Builder setAcAnnual(Float acAnnual);
      @JsonProperty("solrad_annual") public abstract Builder setSolradAnnual(Float solradAnnual);
      
      public abstract Outputs build();
    }
  }
}
