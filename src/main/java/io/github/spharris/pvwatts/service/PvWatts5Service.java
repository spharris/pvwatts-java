package io.github.spharris.pvwatts.service;

import static io.github.spharris.ssc.ExecutionHandlers.messageLoggingHandler;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import io.github.spharris.pvwatts.service.PvWatts5Response.Outputs;
import io.github.spharris.pvwatts.service.PvWatts5Response.SscInfo;
import io.github.spharris.pvwatts.service.PvWatts5Response.StationInfo;
import io.github.spharris.pvwatts.service.weather.WeatherSource;
import io.github.spharris.pvwatts.utils.RequestConverter;
import io.github.spharris.ssc.DataContainer;
import io.github.spharris.ssc.SscModule;
import io.github.spharris.ssc.SscModuleFactory;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Provider;

/** A service for PvWatts5 (pvwattsv5) */
public final class PvWatts5Service {

  private static final String SERVICE_VERSION = "0.0.1";

  private static final String MODULE_NAME = "pvwattsv5";
  private static final PvWatts5Request DEFAULT_REQUEST =
      PvWatts5Request.builder()
          .setDataset("tmy2")
          .setRadius(100)
          .setTimeframe("monthly")
          .setDcAcRatio(1.1f)
          .setGcr(0.4f)
          .setInvEff(96f)
          .build();

  private final SscModuleFactory moduleFactory;
  private final Provider<DataContainer> dataContainerProvider;
  private final ImmutableMap<String, WeatherSource> weatherSources;

  @Inject
  PvWatts5Service(
      SscModuleFactory moduleFactory,
      Provider<DataContainer> dataContainerProvider,
      Map<String, WeatherSource> weatherSources) {
    this.moduleFactory = moduleFactory;
    this.dataContainerProvider = dataContainerProvider;
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

  private PvWatts5Response executeWithResponse(
      PvWatts5Request request, PvWatts5Response.Builder response) {
    try (SscModule module = moduleFactory.create(MODULE_NAME)) {
      try (DataContainer data = dataContainerProvider.get()) {
        setRequiredValues(data);
        Variables.SOLAR_RESOURCE_FILE.set(
            weatherSources
                .get(request.getDataset())
                .getWeatherFile(request.getLat(), request.getLon(), request.getRadius()),
            data);
        Variables.SYSTEM_CAPACITY.set(request.getSystemCapacity(), data);
        Variables.MODULE_TYPE.set(request.getModuleType(), data);
        Variables.LOSSES.set(request.getLosses(), data);
        Variables.ARRAY_TYPE.set(request.getArrayType(), data);
        Variables.TILT.set(request.getTilt(), data);
        Variables.AZIMUTH.set(request.getAzimuth(), data);
        Variables.DC_AC_RATIO.set(request.getDcAcRatio(), data);
        Variables.GCR.set(request.getGcr(), data);
        Variables.INV_EFF.set(request.getInvEff(), data);

        ImmutableList.Builder<String> errorListBuilder = ImmutableList.builder();
        ImmutableList.Builder<String> warningListBuilder = ImmutableList.builder();
        module.execute(data, messageLoggingHandler(errorListBuilder, warningListBuilder));

        ImmutableList<String> errors = errorListBuilder.build();
        if (errors.isEmpty()) {
          buildResponse(module, data, request, response);
        }

        response.setErrors(errors);
        response.setWarnings(warningListBuilder.build());
      }
    }

    return response.build();
  }

  /** Parameters that are always the same for every request, but are still required. */
  private static void setRequiredValues(DataContainer data) {
    Variables.ADJUST_CONSTANT.set(0f, data);
    Variables.ADJUST_FACTOR.set(0f, data);
  }

  /** Set default values on the request if they're not already set */
  private static PvWatts5Request setDefaults(PvWatts5Request request) {
    return request
        .toBuilder()
        .setDataset(Optional.ofNullable(request.getDataset()).orElse(DEFAULT_REQUEST.getDataset()))
        .setRadius(Optional.ofNullable(request.getRadius()).orElse(DEFAULT_REQUEST.getRadius()))
        .setTimeframe(
            Optional.ofNullable(request.getTimeframe()).orElse(DEFAULT_REQUEST.getTimeframe()))
        .setDcAcRatio(
            Optional.ofNullable(request.getDcAcRatio()).orElse(DEFAULT_REQUEST.getDcAcRatio()))
        .setGcr(Optional.ofNullable(request.getGcr()).orElse(DEFAULT_REQUEST.getGcr()))
        .setInvEff(Optional.ofNullable(request.getInvEff()).orElse(DEFAULT_REQUEST.getInvEff()))
        .build();
  }

  private static void buildResponse(
      SscModule module,
      DataContainer data,
      PvWatts5Request request,
      PvWatts5Response.Builder response) {
    response
        .setVersion(SERVICE_VERSION)
        .setSscInfo(
            SscInfo.builder()
                .setVersion(module.getSscVersion())
                .setBuild(module.getSscBuildInfo())
                .build())
        .setStationInfo(
            StationInfo.builder()
                .setLat(Variables.LAT.get(data))
                .setLon(Variables.LON.get(data))
                .setElev(Variables.ELEV.get(data))
                .setTz(Variables.TZ.get(data))
                .setLocation(Variables.LOCATION.get(data))
                .setCity(Variables.CITY.get(data))
                .setState(Variables.STATE.get(data))
                .setSolarResourceFile(
                    Paths.get(Variables.SOLAR_RESOURCE_FILE.get(data)).getFileName().toString())
                .build());

    Float acAnnual = Variables.AC_ANNUAL.get(data);
    Outputs.Builder outputsBuilder =
        Outputs.builder()
            .setPoaMonthly(Variables.POA_MONTHLY.get(data))
            .setDcMonthly(Variables.DC_MONTHLY.get(data))
            .setAcMonthly(Variables.AC_MONTHLY.get(data))
            .setAcAnnual(acAnnual)
            .setSolradMonthly(Variables.SOLRAD_MONTHLY.get(data))
            .setSolradAnnual(Variables.SOLRAD_ANNUAL.get(data))
            .setCapacityFactor(acAnnual / (request.getSystemCapacity() * 8760.0f) * 100.0f);

    if (Objects.equals(request.getTimeframe(), "hourly")) {
      outputsBuilder.setAc(Variables.AC.get(data));
      outputsBuilder.setPoa(Variables.POA.get(data));
      outputsBuilder.setDn(Variables.DN.get(data));
      outputsBuilder.setDc(Variables.DC.get(data));
      outputsBuilder.setDf(Variables.DF.get(data));
      outputsBuilder.setTamb(Variables.TAMB.get(data));
      outputsBuilder.setTcell(Variables.TCELL.get(data));
      outputsBuilder.setWspd(Variables.WSPD.get(data));
    }

    response.setOutputs(outputsBuilder.build());
  }
}
