package io.github.spharris.pvwatts.service;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.multibindings.MapBinder;

import io.github.spharris.pvwatts.service.weather.WeatherSource;
import io.github.spharris.pvwatts.utils.RequestConverter;
import io.github.spharris.ssc.SscModule;

@RunWith(MockitoJUnitRunner.class)
public class IntegrationTestPvWatts4Service {
  
  // https://developer.nrel.gov/api/pvwatts/v4.json?api_key=DEMO_KEY&system_size=4&dataset=tmy2&derate=0.77&lat=33.81666&lon=-118.15&tilt=1.5&track_mode=1&azimuth=30&timeframe=hourly
  
  static final float EPSILON = 0.001f;
  
  final ImmutableMultimap<String, String> urlParameters = ImmutableMultimap.<String, String>builder()
      .put("lat", "33.816")
      .put("lon", "-118.15")
      .put("azimuth", "30")
      .put("tilt", "1.5")
      .put("dataset", "tmy2")
      .put("system_size", "4")
      .put("derate", "0.77")
      .put("track_mode", "1")
      .build();
  
  final PvWatts4Request.Builder requestBuilder =
      RequestConverter.toPvWatts4Request(urlParameters).toBuilder();
  
  final ObjectMapper mapper = new ObjectMapper()
      .registerModule(new GuavaModule());
  
  @Mock WeatherSource tmy2WeatherSource;
  @Inject PvWatts4Service service;
  
  @Before
  public void createInjector() {
    Guice.createInjector(
      new SscModule(),
      new AbstractModule() {
        
        @Override
        protected void configure() {
          MapBinder.<String, WeatherSource>newMapBinder(binder(),
            String.class,
            WeatherSource.class)
          .addBinding("tmy2").toInstance(tmy2WeatherSource);
        }
      }).injectMembers(this);
  }
  
  @Before
  public void returnWeatherData() {
    when(tmy2WeatherSource.getWeatherFile(anyFloat(), anyFloat(), anyInt())).thenReturn(
      "target/test-classes/weather/tmy2/23129.tm2");
  }
  
  @Test
  public void runsPvWatts4Simulation() {
    PvWatts4Response result = service.execute(requestBuilder.build());
    
    assertThat(result.getOutputs().getAcAnnual()).isGreaterThan(0f);
  }
  
  @Test
  public void populatesFilename() {
    PvWatts4Response result = service.execute(urlParameters);
    
    assertThat(result.getStationInfo().getFileName()).isEqualTo("23129.tm2");
  }
  
  @Test
  public void runsPvWatts4SimulationFromParameters() {
    PvWatts4Response result = service.execute(urlParameters);
    
    assertThat(result.getOutputs().getAcAnnual()).isGreaterThan(0f);
  }
  
  @Test
  public void simulationFromParametersWritesInputs() {
    PvWatts4Response result = service.execute(urlParameters);
    
    assertThat(result.getInputs()).isEqualTo(urlParameters);
  }
  
  @Test
  public void doesNotPopulateHourlyDataByDefault() {
    PvWatts4Response result = service.execute(requestBuilder.build());
    
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
    PvWatts4Response result = service.execute(requestBuilder
      .setTimeframe("hourly")
      .build());
    
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
    PvWatts4Response result = service.execute(requestBuilder
      .setTimeframe("hourly")
      .build());
    
    PvWatts4Response expected = loadResponse("long_beach_hourly.json");
    
    arraysAreClose(result.getOutputs().getAc(), expected.getOutputs().getAc());
    arraysAreClose(result.getOutputs().getDc(), expected.getOutputs().getDc());
    arraysAreClose(result.getOutputs().getDf(), expected.getOutputs().getDf());
    arraysAreClose(result.getOutputs().getDn(), expected.getOutputs().getDn());
    arraysAreClose(result.getOutputs().getPoa(), expected.getOutputs().getPoa());
    arraysAreClose(result.getOutputs().getTamb(), expected.getOutputs().getTamb());
    arraysAreClose(result.getOutputs().getTcell(), expected.getOutputs().getTcell());
    arraysAreClose(result.getOutputs().getWspd(), expected.getOutputs().getWspd());
    
    arraysAreClose(result.getOutputs().getAcMonthly(), expected.getOutputs().getAcMonthly());
    arraysAreClose(result.getOutputs().getDcMonthly(), expected.getOutputs().getDcMonthly());
    arraysAreClose(result.getOutputs().getPoaMonthly(), expected.getOutputs().getPoaMonthly());
    
    assertThat(result.getOutputs().getAcAnnual()).isWithin(EPSILON)
      .of(expected.getOutputs().getAcAnnual());
    
    assertThat(result.getOutputs().getSolradAnnual()).isWithin(EPSILON)
      .of(expected.getOutputs().getSolradAnnual());
  }
  
  @Test
  public void monthlyDataMatchesExpected() throws Exception {
    PvWatts4Response result = service.execute(requestBuilder.build());
    
    PvWatts4Response expected = loadResponse("long_beach_monthly.json");
    
    assertThat(result.getOutputs()).isEqualTo(expected.getOutputs());
  }
  
  @Test
  public void errorsAndWarningsIsEmpty() throws Exception {
    PvWatts4Response result = service.execute(requestBuilder.build());
    
    assertThat(result.getErrors()).isEmpty();
    assertThat(result.getWarnings()).isEmpty();
  }
  
  private static void arraysAreClose(ImmutableList<Float> result, ImmutableList<Float> expected) {
    for (int i = 0; i < result.size(); i++) {
      assertThat(result.get(i)).isWithin(EPSILON).of(expected.get(i));
    }
  }
  
  private PvWatts4Response loadResponse(String file) throws Exception {
    return mapper.readValue(
      getClass().getClassLoader().getResourceAsStream("samples/v4/" + file),
      PvWatts4Response.class);
  }
}
