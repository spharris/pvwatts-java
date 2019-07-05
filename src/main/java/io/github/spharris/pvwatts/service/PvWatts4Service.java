package io.github.spharris.pvwatts.service;

import static io.github.spharris.ssc.ExecutionHandlers.messageLoggingHandler;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import io.github.spharris.pvwatts.service.PvWatts4Response.Outputs;
import io.github.spharris.pvwatts.service.PvWatts4Response.SscInfo;
import io.github.spharris.pvwatts.service.PvWatts4Response.StationInfo;
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

/** A service for PvWatts4 (pvwattsv1) */
public final class PvWatts4Service {

  private static final String SERVICE_VERSION = "0.0.1";

  private static final String MODULE_NAME = "pvwattsv1";
  private static final PvWatts4Request DEFAULT_REQUEST =
      PvWatts4Request.builder()
          .setDataset("tmy3")
          .setRadius(100)
          .setTimeframe("monthly")
          .setTiltEqLat(0)
          .setTrackMode(1)
          .build();

  private final SscModuleFactory moduleFactory;
  private final Provider<DataContainer> dataContainerProvider;
  private final ImmutableMap<String, WeatherSource> weatherSources;

  @Inject
  PvWatts4Service(
      SscModuleFactory moduleFactory,
      Provider<DataContainer> dataContainerProvider,
      Map<String, WeatherSource> weatherSources) {
    this.moduleFactory = moduleFactory;
    this.dataContainerProvider = dataContainerProvider;
    this.weatherSources = ImmutableMap.copyOf(weatherSources);
  }

  public PvWatts4Response execute(ImmutableMultimap<String, String> parameters) {
    return executeWithResponse(
        RequestConverter.toPvWatts4Request(parameters),
        PvWatts4Response.builder().setInputs(parameters));
  }

  public PvWatts4Response execute(PvWatts4Request request) {
    return executeWithResponse(setDefaults(request), PvWatts4Response.builder());
  }

  private PvWatts4Response executeWithResponse(
      PvWatts4Request request, PvWatts4Response.Builder response) {
    SscModule module = moduleFactory.create(MODULE_NAME);
    DataContainer data = dataContainerProvider.get();

    setRequiredValues(data);
    Variables.SOLAR_RESOURCE_FILE.set(
        weatherSources
            .get(request.getDataset())
            .getWeatherFile(request.getLat(), request.getLon(), request.getRadius()),
        data);
    Variables.SYSTEM_SIZE.set(request.getSystemSize(), data);
    Variables.AZIMUTH.set(request.getAzimuth(), data);
    Variables.TILT.set(request.getTilt(), data);
    Variables.DERATE.set(request.getDerate(), data);
    Variables.TRACK_MODE.set(request.getTrackMode(), data);
    Variables.TILT_EQ_LAT.set(request.getTiltEqLat(), data);
    Variables.INOCT.set(request.getInoct(), data);
    Variables.GAMMA.set(request.getGamma(), data);

    ImmutableList.Builder<String> errorListBuilder = ImmutableList.builder();
    ImmutableList.Builder<String> warningListBuilder = ImmutableList.builder();
    module.execute(data, messageLoggingHandler(errorListBuilder, warningListBuilder));

    ImmutableList<String> errors = errorListBuilder.build();
    if (errors.isEmpty()) {
      populateResponse(module, data, request, response);
    }

    response.setErrors(errors);
    response.setWarnings(warningListBuilder.build());

    module.free();
    data.free();

    return response.build();
  }

  /** Parameters that are always the same for every request, but are still required. */
  private static void setRequiredValues(DataContainer data) {
    Variables.ADJUST_CONSTANT.set(1f, data);
    Variables.ADJUST_FACTOR.set(1f, data);
  }

  /** Set default values on the request if they're not already set */
  private static PvWatts4Request setDefaults(PvWatts4Request request) {
    return request
        .toBuilder()
        .setDataset(Optional.ofNullable(request.getDataset()).orElse(DEFAULT_REQUEST.getDataset()))
        .setRadius(Optional.ofNullable(request.getRadius()).orElse(DEFAULT_REQUEST.getRadius()))
        .setTimeframe(
            Optional.ofNullable(request.getTimeframe()).orElse(DEFAULT_REQUEST.getTimeframe()))
        .setTiltEqLat(
            Optional.ofNullable(request.getTiltEqLat()).orElse(DEFAULT_REQUEST.getTiltEqLat()))
        .setTrackMode(
            Optional.ofNullable(request.getTrackMode()).orElse(DEFAULT_REQUEST.getTrackMode()))
        .build();
  }

  private static void populateResponse(
      SscModule module,
      DataContainer data,
      PvWatts4Request request,
      PvWatts4Response.Builder response) {
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
                .setFileName(
                    Paths.get(Variables.SOLAR_RESOURCE_FILE.get(data)).getFileName().toString())
                .build());

    Outputs.Builder outputsBuilder =
        Outputs.builder()
            .setPoaMonthly(Variables.POA_MONTHLY.get(data))
            .setDcMonthly(Variables.DC_MONTHLY.get(data))
            .setAcMonthly(Variables.AC_MONTHLY.get(data))
            .setAcAnnual(Variables.AC_ANNUAL.get(data))
            .setSolradMonthly(Variables.SOLRAD_MONTHLY.get(data))
            .setSolradAnnual(Variables.SOLRAD_ANNUAL.get(data));

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
