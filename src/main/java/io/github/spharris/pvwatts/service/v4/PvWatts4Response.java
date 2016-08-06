package io.github.spharris.pvwatts.service.v4;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class PvWatts4Response {
  
  private final ImmutableMap<String, String> inputs;
  private final ImmutableList<String> errors;
  private final ImmutableList<String> warnings;
  
  private final String version;
  private final SscInfo sscInfo;
  
  private final StationInfo stationInfo;
  
  private final Outputs outputs;
  
  private PvWatts4Response(ImmutableMap<String, String> inputs,
                           ImmutableList<String> errors,
                           ImmutableList<String> warnings,
                           String version,
                           SscInfo sscInfo,
                           StationInfo stationInfo,
                           Outputs outputs) {
    this.inputs = inputs;
    this.errors = errors;
    this.warnings = warnings;
    this.version = version;
    this.sscInfo = sscInfo;
    this.stationInfo = stationInfo;
    this.outputs = outputs;
  }
  
  public ImmutableMap<String, String> getInputs() {
    return inputs;
  }

  public ImmutableList<String> getErrors() {
    return errors;
  }

  public ImmutableList<String> getWarnings() {
    return warnings;
  }

  public String getVersion() {
    return version;
  }

  public SscInfo getSscInfo() {
    return sscInfo;
  }

  public StationInfo getStationInfo() {
    return stationInfo;
  }

  public Outputs getOutputs() {
    return outputs;
  }

  public static Builder newBuilder() {
      return new Builder();
    }
    
  public static final class Builder {
    private ImmutableMap<String, String> inputs;
    private ImmutableList<String> errors;
    private ImmutableList<String> warnings;
    private String version;
    private SscInfo sscInfo;
    private StationInfo stationInfo;
    private Outputs outputs;
    
    public PvWatts4Response build() {
      return new PvWatts4Response(inputs,
        errors,
        warnings,
        version,
        sscInfo,
        stationInfo,
        outputs);
    }

    public Builder setInputs(ImmutableMap<String, String> inputs) {
      this.inputs = inputs;
      return this;
    }

    public Builder setErrors(ImmutableList<String> errors) {
      this.errors = errors;
      return this;
    }

    public Builder setWarnings(ImmutableList<String> warnings) {
      this.warnings = warnings;
      return this;
    }

    public Builder setVersion(String version) {
      this.version = version;
      return this;
    }

    public Builder setSscInfo(SscInfo sscInfo) {
      this.sscInfo = sscInfo;
      return this;
    }

    public Builder setStationInfo(StationInfo stationInfo) {
      this.stationInfo = stationInfo;
      return this;
    }

    public Builder setOutputs(Outputs outputs) {
      this.outputs = outputs;
      return this;
    }
  }
  
  public static final class SscInfo {
    private final int version;
    private final String build;
    
    private SscInfo(int version, String build) {
      this.version = version;
      this.build = build;
    }
    
    public int getVersion() {
      return version;
    }
    
    public String getBuild() {
      return build;
    }
    
    public static Builder newBuilder() {
      return new Builder();
    }
    
    public static final class Builder {
      
      private int version;
      private String build;
      
      private Builder() {}
      
      public Builder setVersion(int version) {
        this.version = version;
        return this;
      }
      
      public Builder setBuild(String build) {
        checkNotNull(build);
        
        this.build = build;
        return this;
      }
      
      public SscInfo build() {
        return new SscInfo(version, build);
      }
    }
  }
  
  public static final class StationInfo {
    
    private final float lat;
    private final float lon;
    private final float elev;
    private final float tz;
    private final String location;
    private final String city;
    private final String state;
    private final String fileName;
    private final int distance;
    
    private StationInfo(float lat,
                        float lon,
                        float elev,
                        float tz,
                        String location,
                        String city,
                        String state,
                        String fileName,
                        int distance) {
      this.lat = lat;
      this.lon = lon;
      this.elev = elev;
      this.tz = tz;
      this.location = location;
      this.city = city;
      this.state = state;
      this.fileName = fileName;
      this.distance = distance;
    }
    
    public float getLat() {
      return lat;
    }

    public float getLon() {
      return lon;
    }

    public float getElev() {
      return elev;
    }

    public float getTz() {
      return tz;
    }

    public String getLocation() {
      return location;
    }

    public String getCity() {
      return city;
    }

    public String getState() {
      return state;
    }

    public String getFileName() {
      return fileName;
    }

    public int getDistance() {
      return distance;
    }

    public static Builder newBuilder() {
      return new Builder();
    }

    public static final class Builder {
      
      private float lat;
      private float lon;
      private float elev;
      private float tz;
      private String location;
      private String city;
      private String state;
      private String fileName;
      private int distance;
      
      private Builder() {}

      public StationInfo build() {
        return new StationInfo(lat,
          lon,
          elev,
          tz,
          location,
          city,
          state,
          fileName,
          distance);
      }
      
      public Builder setLat(float lat) {
        this.lat = lat;
        return this;
      }

      public Builder setLon(float lon) {
        this.lon = lon;
        return this;
      }

      public Builder setElev(float elev) {
        this.elev = elev;
        return this;
      }

      public Builder setTz(float tz) {
        this.tz = tz;
        return this;
      }

      public Builder setLocation(String location) {
        this.location = location;
        return this;
      }

      public Builder setCity(String city) {
        this.city = city;
        return this;
      }

      public Builder setState(String state) {
        this.state = state;
        return this;
      }

      public Builder setFileName(String fileName) {
        this.fileName = fileName;
        return this;
      }

      public Builder setDistance(int distance) {
        this.distance = distance;
        return this;
      }
    }
  }
  
  public static final class Outputs {
    
    private final ImmutableList<Float> poaMonthly;
    private final ImmutableList<Float> dcMonthly;
    private final ImmutableList<Float> acMonthly;
    private final float acAnnual;
    
    private final ImmutableList<Float> solradMonthly;
    private final float solradAnnual;
    
    private final ImmutableList<Float> ac;
    private final ImmutableList<Float> poa;
    private final ImmutableList<Float> dn;
    private final ImmutableList<Float> dc;
    private final ImmutableList<Float> df;
    private final ImmutableList<Float> tamb;
    private final ImmutableList<Float> tcell;
    private final ImmutableList<Float> wspd;
    
    private Outputs(ImmutableList<Float> poaMonthly,
                    ImmutableList<Float> dcMonthly,
                    ImmutableList<Float> acMonthly,
                    float acAnnual,
                    ImmutableList<Float> solradMonthly,
                    float solradAnnual,
                    ImmutableList<Float> ac,
                    ImmutableList<Float> poa,
                    ImmutableList<Float> dn,
                    ImmutableList<Float> dc,
                    ImmutableList<Float> df,
                    ImmutableList<Float> tamb,
                    ImmutableList<Float> tcell,
                    ImmutableList<Float> wspd) {
      this.poaMonthly = poaMonthly;
      this.dcMonthly = dcMonthly;
      this.acMonthly = acMonthly;
      this.acAnnual = acAnnual;
      this.solradMonthly = solradMonthly;
      this.solradAnnual = solradAnnual;
      this.ac = ac;
      this.poa = poa;
      this.dn = dn;
      this.dc = dc;
      this.df = df;
      this.tamb = tamb;
      this.tcell = tcell;
      this.wspd = wspd;
    }
    
    public ImmutableList<Float> getPoaMonthly() {
      return poaMonthly;
    }

    public ImmutableList<Float> getDcMonthly() {
      return dcMonthly;
    }

    public ImmutableList<Float> getAcMonthly() {
      return acMonthly;
    }

    public float getAcAnnual() {
      return acAnnual;
    }

    public ImmutableList<Float> getSolradMonthly() {
      return solradMonthly;
    }

    public float getSolradAnnual() {
      return solradAnnual;
    }

    public ImmutableList<Float> getAc() {
      return ac;
    }

    public ImmutableList<Float> getPoa() {
      return poa;
    }

    public ImmutableList<Float> getDn() {
      return dn;
    }

    public ImmutableList<Float> getDc() {
      return dc;
    }

    public ImmutableList<Float> getDf() {
      return df;
    }

    public ImmutableList<Float> getTamb() {
      return tamb;
    }

    public ImmutableList<Float> getTcell() {
      return tcell;
    }

    public ImmutableList<Float> getWspd() {
      return wspd;
    }

    public static Builder newBuilder() {
      return new Builder();
    }

    public static final class Builder {
      private ImmutableList<Float> poaMonthly;
      private ImmutableList<Float> dcMonthly;
      private ImmutableList<Float> acMonthly;
      private float acAnnual;
      private ImmutableList<Float> solradMonthly;
      private float solradAnnual;
      private ImmutableList<Float> ac;
      private ImmutableList<Float> poa;
      private ImmutableList<Float> dn;
      private ImmutableList<Float> dc;
      private ImmutableList<Float> df;
      private ImmutableList<Float> tamb;
      private ImmutableList<Float> tcell;
      private ImmutableList<Float> wspd;
      
      private Builder() {}
      
      public Outputs build() {
        return new Outputs(poaMonthly,
          dcMonthly,
          acMonthly,
          acAnnual,
          solradMonthly,
          solradAnnual,
          ac,
          poa,
          dn,
          dc,
          df,
          tamb,
          tcell,
          wspd);
      }

      public Builder setPoaMonthly(ImmutableList<Float> poaMonthly) {
        this.poaMonthly = poaMonthly;
        return this;
      }

      public Builder setDcMonthly(ImmutableList<Float> dcMonthly) {
        this.dcMonthly = dcMonthly;
        return this;
      }

      public Builder setAcMonthly(ImmutableList<Float> acMonthly) {
        this.acMonthly = acMonthly;
        return this;
      }

      public Builder setAcAnnual(float acAnnual) {
        this.acAnnual = acAnnual;
        return this;
      }

      public Builder setSolradMonthly(ImmutableList<Float> solradMonthly) {
        this.solradMonthly = solradMonthly;
        return this;
      }

      public Builder setSolradAnnual(float solradAnnual) {
        this.solradAnnual = solradAnnual;
        return this;
      }

      public Builder setAc(ImmutableList<Float> ac) {
        this.ac = ac;
        return this;
      }

      public Builder setPoa(ImmutableList<Float> poa) {
        this.poa = poa;
        return this;
      }

      public Builder setDn(ImmutableList<Float> dn) {
        this.dn = dn;
        return this;
      }

      public Builder setDc(ImmutableList<Float> dc) {
        this.dc = dc;
        return this;
      }

      public Builder setDf(ImmutableList<Float> df) {
        this.df = df;
        return this;
      }

      public Builder setTamb(ImmutableList<Float> tamb) {
        this.tamb = tamb;
        return this;
      }

      public Builder setTcell(ImmutableList<Float> tcell) {
        this.tcell = tcell;
        return this;
      }

      public Builder setWspd(ImmutableList<Float> wspd) {
        this.wspd = wspd;
        return this;
      }
    }
  }
}
