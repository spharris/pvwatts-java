package io.github.spharris.pvwatts.service;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.multibindings.MapBinder;
import io.github.spharris.pvwatts.service.weather.WeatherSource;
import io.github.spharris.pvwatts.utils.RequestConverter;
import io.github.spharris.ssc.SscGuiceModule;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class IntegrationTestPvWatts5Service {

  // https://developer.nrel.gov/api/pvwatts/v5.json?api_key=DEMO_KEY&system_capacity=4&dataset=tmy2&lat=33.81666&lon=-118.15&tilt=1.5&track_mode=1&azimuth=30&array_type=0&module_type=0&losses=15

  private static final ImmutableMultimap<String, String> URL_PARAMETERS =
      ImmutableMultimap.<String, String>builder()
          .put("lat", "33.816")
          .put("lon", "-118.15")
          .put("azimuth", "30")
          .put("tilt", "1.5")
          .put("dataset", "tmy2")
          .put("system_capacity", "4")
          .put("losses", "15f")
          .put("array_type", "0")
          .put("module_type", "0")
          .build();

  private final PvWatts5Request.Builder requestBuilder =
      RequestConverter.toPvWatts5Request(URL_PARAMETERS).toBuilder();

  private final ObjectMapper mapper = new ObjectMapper().registerModule(new GuavaModule());

  @Mock private WeatherSource tmy2WeatherSource;
  @Inject private PvWatts5Service service;

  @Before
  public void createInjector() {
    Guice.createInjector(
            new SscGuiceModule(),
            new AbstractModule() {

              @Override
              protected void configure() {
                MapBinder.<String, WeatherSource>newMapBinder(
                        binder(), String.class, WeatherSource.class)
                    .addBinding("tmy2")
                    .toInstance(tmy2WeatherSource);
              }
            })
        .injectMembers(this);
  }

  @Before
  public void returnWeatherData() {
    when(tmy2WeatherSource.getWeatherFile(anyFloat(), anyFloat(), anyInt()))
        .thenReturn("target/test-classes/weather/tmy2/23129.tm2");
  }

  @Test
  public void runsPvWatts5Simulation() {
    PvWatts5Response result = service.execute(requestBuilder.build());

    assertThat(result.getOutputs().getAcAnnual()).isGreaterThan(0f);
  }

  @Test
  public void populatesFilename() {
    PvWatts5Response result = service.execute(URL_PARAMETERS);

    assertThat(result.getStationInfo().getSolarResourceFile()).isEqualTo("23129.tm2");
  }

  @Test
  public void runsPvWatts5SimulationFromParameters() {
    PvWatts5Response result = service.execute(URL_PARAMETERS);

    assertThat(result.getErrors()).isEmpty();
    assertThat(result.getOutputs().getAcAnnual()).isGreaterThan(0f);
  }

  @Test
  public void simulationFromParametersWritesInputs() {
    PvWatts5Response result = service.execute(URL_PARAMETERS);

    assertThat(result.getInputs()).isEqualTo(URL_PARAMETERS);
  }

  @Test
  public void doesNotPopulateHourlyDataByDefault() {
    PvWatts5Response result = service.execute(requestBuilder.build());

    assertThat(result.getOutputs().getAc()).isNull();
    assertThat(result.getOutputs().getDc()).isNull();
    assertThat(result.getOutputs().getDf()).isNull();
    assertThat(result.getOutputs().getDn()).isNull();
    assertThat(result.getOutputs().getPoa()).isNull();
    assertThat(result.getOutputs().getTamb()).isNull();
    assertThat(result.getOutputs().getTcell()).isNull();
    assertThat(result.getOutputs().getWspd()).isNull();
  }

  @Test
  public void populatesHourlyDataWhenRequested() {
    PvWatts5Response result = service.execute(requestBuilder.setTimeframe("hourly").build());

    assertThat(result.getOutputs().getAc()).hasSize(8760);
    assertThat(result.getOutputs().getDc()).hasSize(8760);
    assertThat(result.getOutputs().getDf()).hasSize(8760);
    assertThat(result.getOutputs().getDn()).hasSize(8760);
    assertThat(result.getOutputs().getPoa()).hasSize(8760);
    assertThat(result.getOutputs().getTamb()).hasSize(8760);
    assertThat(result.getOutputs().getTcell()).hasSize(8760);
    assertThat(result.getOutputs().getWspd()).hasSize(8760);
  }

  @Test
  public void hourlyDataMatchesExpected() throws Exception {
    PvWatts5Response result = service.execute(requestBuilder.setTimeframe("hourly").build());

    PvWatts5Response expected = loadResponse("long_beach_hourly.json");

    assertArraysAreClose(result.getOutputs().getAc(), expected.getOutputs().getAc());
    assertArraysAreClose(result.getOutputs().getDc(), expected.getOutputs().getDc());
    assertArraysAreClose(result.getOutputs().getDf(), expected.getOutputs().getDf());
    assertArraysAreClose(result.getOutputs().getDn(), expected.getOutputs().getDn());
    assertArraysAreClose(result.getOutputs().getPoa(), expected.getOutputs().getPoa());
    assertArraysAreClose(result.getOutputs().getTamb(), expected.getOutputs().getTamb());
    assertArraysAreClose(result.getOutputs().getTcell(), expected.getOutputs().getTcell());
    assertArraysAreClose(result.getOutputs().getWspd(), expected.getOutputs().getWspd());

    assertArraysAreClose(result.getOutputs().getAcMonthly(), expected.getOutputs().getAcMonthly());
    assertArraysAreClose(result.getOutputs().getDcMonthly(), expected.getOutputs().getDcMonthly());
    assertArraysAreClose(
        result.getOutputs().getPoaMonthly(), expected.getOutputs().getPoaMonthly());

    assertValuesAreClose(result.getOutputs().getAcAnnual(), expected.getOutputs().getAcAnnual());
    assertValuesAreClose(
        result.getOutputs().getSolradAnnual(), expected.getOutputs().getSolradAnnual());

    // Skip capacity factor because it seems to be calculated differently in this version
    // of SSC
    assertValuesAreClose(
        result.getOutputs().getCapacityFactor(), expected.getOutputs().getCapacityFactor());
  }

  @Test
  public void monthlyDataMatchesExpected() throws Exception {
    PvWatts5Response result = service.execute(requestBuilder.build());

    PvWatts5Response expected = loadResponse("long_beach_monthly.json");

    assertArraysAreClose(result.getOutputs().getAcMonthly(), expected.getOutputs().getAcMonthly());
    assertArraysAreClose(result.getOutputs().getDcMonthly(), expected.getOutputs().getDcMonthly());
    assertArraysAreClose(
        result.getOutputs().getPoaMonthly(), expected.getOutputs().getPoaMonthly());

    assertValuesAreClose(result.getOutputs().getAcAnnual(), expected.getOutputs().getAcAnnual());

    assertValuesAreClose(
        result.getOutputs().getSolradAnnual(), expected.getOutputs().getSolradAnnual());

    // Skip capacity factor because it seems to be calculated differently in this version
    // of SSC
    assertValuesAreClose(
        result.getOutputs().getCapacityFactor(), expected.getOutputs().getCapacityFactor());
  }

  @Test
  public void errorsAndWarningsIsEmpty() {
    PvWatts5Response result = service.execute(requestBuilder.build());

    assertThat(result.getErrors()).isEmpty();
    assertThat(result.getWarnings()).isEmpty();
  }

  private static void assertArraysAreClose(
      ImmutableList<Float> result, ImmutableList<Float> expected) {
    for (int i = 0; i < result.size(); i++) {
      assertValuesAreClose(result.get(i), expected.get(i));
    }
  }

  private static void assertValuesAreClose(float left, float right) {
    if (Math.abs(left) < 0.1f || Math.abs(right) < 0.1f) {
      assertThat(left).isWithin(0.1f).of(right);
      return;
    }

    float difference = Math.abs(left / right - 1.0f);
    assertWithMessage("Expected %s and %s to be within 5% of each other.", left, right)
        .that(difference)
        .isLessThan(0.05f);
  }

  private PvWatts5Response loadResponse(String file) throws Exception {
    return mapper.readValue(
        getClass().getClassLoader().getResourceAsStream("samples/v5/" + file),
        PvWatts5Response.class);
  }
}
