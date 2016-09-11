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
  
  static final PvWatts4Request.Builder REQUEST_BUILDER = PvWatts4Request.builder()
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
    PvWatts4Response result = service.execute(REQUEST_BUILDER.build());
    
    assertThat(result.getOutputs().getAcAnnual()).isGreaterThan(0f);
  }
  
  @Test
  public void doesNotPopulateHourlyDataByDefault() {
    
  }
}
