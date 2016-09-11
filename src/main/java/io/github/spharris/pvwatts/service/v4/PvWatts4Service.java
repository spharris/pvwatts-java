package io.github.spharris.pvwatts.service.v4;

import java.util.Objects;

import javax.inject.Inject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;

import io.github.spharris.pvwatts.service.v4.PvWatts4Response.Outputs;
import io.github.spharris.pvwatts.service.v4.PvWatts4Response.SscInfo;
import io.github.spharris.pvwatts.service.v4.PvWatts4Response.StationInfo;
import io.github.spharris.pvwatts.utils.RequestConverter;
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

  public PvWatts4Response execute(ImmutableMultimap<String, String> parameters) {
    return executeWithResponse(
      RequestConverter.toPvWatts4Request(parameters),
      PvWatts4Response.builder().setInputs(parameters));
  }
  
  public PvWatts4Response execute(PvWatts4Request request) {
    return executeWithResponse(request, PvWatts4Response.builder());
  }
  
  private PvWatts4Response executeWithResponse(PvWatts4Request request,
      PvWatts4Response.Builder response) {
    Module module = moduleFactory.create(MODULE_NAME);

    setRequiredValues(module);
    Variables.SOLAR_RESOURCE_FILE.set("target/classes/weather/23129.tm2", module);
    Variables.SYSTEM_SIZE.set(request.getSystemSize(), module);
    Variables.AZIMUTH.set(request.getAzimuth(), module);
    Variables.TILT.set(request.getTilt(), module);
    Variables.DERATE.set(request.getDerate(), module);
    Variables.TRACK_MODE.set(request.getTrackMode(), module);
    Variables.TILT_EQ_LAT.set(request.getTiltEqLat(), module);
    Variables.INOCT.set(request.getInoct(), module);
    Variables.GAMMA.set(request.getGamma(), module);

    ImmutableList.Builder<String> errorListBuilder = ImmutableList.builder();
    ImmutableList.Builder<String> warningListBuilder = ImmutableList.builder();
    module.execute(messageLoggingHandler(errorListBuilder, warningListBuilder));
    
    ImmutableList<String> errors = errorListBuilder.build();
    if (errors.isEmpty()) {
      buildResponse(module, request, response);
    }
    
    response.setErrors(errors);
    response.setWarnings(warningListBuilder.build());
    
    module.free();
    
    return response.build();
  }
  
  /**
   * Parameters that are always the same for every request, but are still required.
   */
  private static void setRequiredValues(Module module) {
    Variables.ADJUST_CONSTANT.set(1f, module);
  }
  
  private static PvWatts4Response.Builder buildResponse(Module module, PvWatts4Request request,
      PvWatts4Response.Builder response) {
    response.setVersion(SERVICE_VERSION)
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
          .build());
    
    Outputs.Builder outputsBuilder = Outputs.builder()
        .setPoaMonthly(Variables.POA_MONTHLY.get(module))
        .setDcMonthly(Variables.DC_MONTHLY.get(module))
        .setAcMonthly(Variables.AC_MONTHLY.get(module))
        .setAcAnnual(Variables.AC_ANNUAL.get(module))
        .setSolradMonthly(Variables.SOLRAD_MONTHLY.get(module))
        .setSolradAnnual(Variables.SOLRAD_ANNUAL.get(module));
    
    if (Objects.equals(request.getTimeframe(), "hourly")) {
      outputsBuilder.setAc(Variables.AC.get(module));
      outputsBuilder.setPoa(Variables.POA.get(module));
      outputsBuilder.setDn(Variables.DN.get(module));
      outputsBuilder.setDc(Variables.DC.get(module));
      outputsBuilder.setDf(Variables.DF.get(module));
      outputsBuilder.setTamb(Variables.TAMB.get(module));
      outputsBuilder.setTcell(Variables.TCELL.get(module));
      outputsBuilder.setWspd(Variables.WSPD.get(module));
    }

    response.setOutputs(outputsBuilder.build());
    
    return response;
  }
  
  private static ExecutionHandler messageLoggingHandler(
      final ImmutableList.Builder<String> errorListBuilder,
      final ImmutableList.Builder<String> warningListBuilder) {
    return new ExecutionHandler() {

      @Override
      public boolean handleLogMessage(MessageType type, float time, String message) {
        if (type == MessageType.ERROR || type == MessageType.NOTICE) {
          errorListBuilder.add(message);
        } else if (type == MessageType.WARNING) {
          warningListBuilder.add(message);
        }
        
        return true;
      }

      @Override
      public boolean handleProgressUpdate(float percentComplete, float time, String text) {
        return true;
      }
    };
  }
}
