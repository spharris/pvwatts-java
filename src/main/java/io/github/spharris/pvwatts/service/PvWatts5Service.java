package io.github.spharris.pvwatts.service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;

import io.github.spharris.pvwatts.service.PvWatts5Response.Outputs;
import io.github.spharris.pvwatts.service.PvWatts5Response.SscInfo;
import io.github.spharris.pvwatts.service.PvWatts5Response.StationInfo;
import io.github.spharris.pvwatts.service.weather.WeatherSource;
import io.github.spharris.pvwatts.utils.RequestConverter;
import io.github.spharris.ssc.ExecutionHandler;
import io.github.spharris.ssc.Module;
import io.github.spharris.ssc.ModuleFactory;

public final class PvWatts5Service {
  
  public static final String SERVICE_VERSION = "0.0.1";
  
  private static final String MODULE_NAME = "pvwattsv5"; 
  private static final PvWatts5Request DEFAULT_REQUEST = PvWatts5Request.builder()
      .setRadius(100)
      .setTimeframe("monthly")
      .setDcAcRatio(1.1f)
      .setGcr(0.4f)
      .setInvEff(96f)
      .build();
  
  private final ModuleFactory moduleFactory;
  private ImmutableMap<String, WeatherSource> weatherSources;
  
  @Inject
  public PvWatts5Service(
      ModuleFactory moduleFactory,
      Map<String, WeatherSource> weatherSources) {
    this.moduleFactory = moduleFactory;
    this.weatherSources = ImmutableMap.copyOf(weatherSources);
  }

  public PvWatts5Response execute(ImmutableMultimap<String, String> parameters) {
    return executeWithResponse(
      RequestConverter.toPvWatts5Request(parameters),
      PvWatts5Response.builder().setInputs(parameters));
  }
  
  public PvWatts5Response execute(PvWatts5Request request) {
    return executeWithResponse(setDefaults(request), PvWatts5Response.builder());
  }
  
  private PvWatts5Response executeWithResponse(PvWatts5Request request,
      PvWatts5Response.Builder response) {
    Module module = moduleFactory.create(MODULE_NAME);

    setRequiredValues(module);
    Variables.SOLAR_RESOURCE_FILE.set(weatherSources.get("tmy2").getWeatherFile(
      request.getLat(), request.getLon(), request.getRadius()), module);
    Variables.SYSTEM_CAPACITY.set(request.getSystemCapacity(), module);
    Variables.MODULE_TYPE.set(request.getModuleType(), module);
    Variables.LOSSES.set(request.getLosses(), module);
    Variables.ARRAY_TYPE.set(request.getArrayType(), module);
    Variables.TILT.set(request.getTilt(), module);
    Variables.AZIMUTH.set(request.getAzimuth(), module);
    Variables.DC_AC_RATIO.set(request.getDcAcRatio(), module);
    Variables.GCR.set(request.getGcr(), module);
    Variables.INV_EFF.set(request.getInvEff(), module);

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
  
  /**
   * Set default values on the request if they're not already set
   */
  private static PvWatts5Request setDefaults(PvWatts5Request request) {
    return request.toBuilder()
      .setRadius(Optional.ofNullable(request.getRadius()).orElse(DEFAULT_REQUEST.getRadius()))
      .setTimeframe(
        Optional.ofNullable(request.getTimeframe()).orElse(DEFAULT_REQUEST.getTimeframe()))
      .setDcAcRatio(
        Optional.ofNullable(request.getDcAcRatio()).orElse(DEFAULT_REQUEST.getDcAcRatio()))
      .setGcr(Optional.ofNullable(request.getGcr()).orElse(DEFAULT_REQUEST.getGcr()))
      .setInvEff(Optional.ofNullable(request.getInvEff()).orElse(DEFAULT_REQUEST.getInvEff()))
      .build();
  }
  
  private static PvWatts5Response.Builder buildResponse(Module module, PvWatts5Request request,
      PvWatts5Response.Builder response) {
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
          .setSolarResourceFile(Iterables.getLast(Splitter.on("/").splitToList(
              Variables.SOLAR_RESOURCE_FILE.get(module))))
          .build());
    
    Outputs.Builder outputsBuilder = Outputs.builder()
        .setPoaMonthly(Variables.POA_MONTHLY.get(module))
        .setDcMonthly(Variables.DC_MONTHLY.get(module))
        .setAcMonthly(Variables.AC_MONTHLY.get(module))
        .setAcAnnual(Variables.AC_ANNUAL.get(module))
        .setSolradMonthly(Variables.SOLRAD_MONTHLY.get(module))
        .setSolradAnnual(Variables.SOLRAD_ANNUAL.get(module))
        .setCapacityFactor(Variables.CAPACITY_FACTOR.get(module));
    
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
