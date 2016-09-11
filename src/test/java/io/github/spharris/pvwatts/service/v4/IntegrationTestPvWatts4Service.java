package io.github.spharris.pvwatts.service.v4;

import static com.google.common.truth.Truth.assertThat;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.inject.Guice;

import io.github.spharris.pvwatts.service.PvWattsServiceModule;
import io.github.spharris.ssc.SscModule;

@RunWith(JUnit4.class)
public class IntegrationTestPvWatts4Service {
  
  PvWatts4Request.Builder requestBuilder = PvWatts4Request.builder()
        .setLat(38f)
        .setLon(-118f)
        .setAzimuth(180f)
        .setTilt(10f)
        .setDataset("tmy2")
        .setSystemSize(5f)
        .setDerate(0.85f)
        .setTrackMode(1); 
  
  @Inject PvWatts4Service service;
  
  @Before
  public void createInjector() {
    Guice.createInjector(
            new SscModule(),
            new PvWattsServiceModule())
        .injectMembers(this);
  }
  
  @Test
  public void runsPvWatts4Simulation() {
    PvWatts4Response result = service.execute(requestBuilder.build());
    
    assertThat(result.getOutputs().getAcAnnual()).isGreaterThan(0f);
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
}
