package io.github.spharris.pvwatts.service.v4;

import javax.inject.Inject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;

import io.github.spharris.pvwatts.service.v4.PvWatts4Response.Outputs;
import io.github.spharris.pvwatts.service.v4.PvWatts4Response.SscInfo;
import io.github.spharris.pvwatts.service.v4.PvWatts4Response.StationInfo;
import io.github.spharris.ssc.ExecutionHandler;
import io.github.spharris.ssc.Module;
import io.github.spharris.ssc.ModuleFactory;

public final class PvWatts4Service {
  
  public static final String SERVICE_VERSION = "0.0.1";
  
  private static final String MODULE_NAME = "pvwattsv1"; 
  
  private final ModuleFactory moduleFactory;
  
  @Inject
  public PvWatts4Service(ModuleFactory moduleFactory) {
    this.moduleFactory = moduleFactory;
  }
  
  public PvWatts4Response execute(ImmutableMultimap<String, String> request) {
    return null;
  }
  
  public PvWatts4Response execute(PvWatts4Request request) {
    Module module = moduleFactory.create(MODULE_NAME);
    
    Variables.SOLAR_RESOURCE_FILE.set("target/classes/weather/23129.tm2", module);
    
    if (request.getSystemSize() != null) {
      Variables.SYSTEM_SIZE.set(request.getSystemSize(), module);
    }
    
    if (request.getAzimuth() != null) {
      Variables.AZIMUTH.set(request.getAzimuth(), module);
    }
    
    if (request.getTilt() != null) {
      Variables.TILT.set(request.getTilt(), module);
    }
    
    if (request.getDerate() != null) {
      Variables.DERATE.set(request.getDerate(), module);
    }
    
    if (request.getTrackMode() != null) {
      Variables.TRACK_MODE.set(request.getTrackMode(), module);
    }
    
    if (request.getTiltEqLat() != null) {
      Variables.TILT_EQ_LAT.set(request.getTiltEqLat(), module);
    }
    
    if (request.getInoct() != null) {
      Variables.INOCT.set(request.getInoct(), module);
    }
    
    if (request.getGamma() != null) {
      Variables.GAMMA.set(request.getGamma(), module);
    }
    
    ImmutableList.Builder<String> builder = ImmutableList.builder();
    setRequiredValues(module);
    module.execute(logMessagesToList(builder));
    
    ImmutableList<String> errors = builder.build();
    PvWatts4Response.Builder response = PvWatts4Response.builder(); 
    if (errors.isEmpty()) {
      buildResponse(module, request);
    }
    response.setErrors(errors);
    
    module.free();
    
    return response.build();
  }
  
  private static void setRequiredValues(Module module) {
    Variables.ADJUST_CONSTANT.set(1f, module);
  }
  
  private static PvWatts4Response.Builder buildResponse(Module module, PvWatts4Request request) {
    PvWatts4Response.Builder builder = PvWatts4Response.builder()
        .setVersion(SERVICE_VERSION)
        .setSscInfo(SscInfo.builder()
          .setVersion(module.getSscVersion())
          .setBuild(module.getSscBuildInfo())
          .build())
        .setStationInfo(StationInfo.builder()
          .setLat(Variables.LAT.get(module))
          .setLon(Variables.LON.get(module))
          .setElev(Variables.ELEV.get(module))
          .setTz(Variables.TZ.get(module))
          .setLocation(Variables.LOCATION.get(module))
          .setCity(Variables.CITY.get(module))
          .setState(Variables.STATE.get(module))
          .build())
        .setOutputs(Outputs.builder()
          .setPoaMonthly(Variables.POA_MONTHLY.get(module))
          .setDcMonthly(Variables.DC_MONTHLY.get(module))
          .setAcMonthly(Variables.AC_MONTHLY.get(module))
          .setAcAnnual(Variables.AC_ANNUAL.get(module))
          .setSolradMonthly(Variables.SOLRAD_MONTHLY.get(module))
          .setSolradAnnual(Variables.SOLRAD_ANNUAL.get(module))
          .build()); 
    
    return builder;
  }
  
  private static ExecutionHandler logMessagesToList(final ImmutableList.Builder<String> builder) {
    return new ExecutionHandler() {

      @Override
      public boolean handleLogMessage(MessageType type, float time, String message) {
        builder.add(message);
        
        return true;
      }

      @Override
      public boolean handleProgressUpdate(float percentComplete, float time, String text) {
        return true;
      }
    };
  }
}
