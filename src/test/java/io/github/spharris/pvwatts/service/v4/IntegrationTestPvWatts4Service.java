package io.github.spharris.pvwatts.service.v4;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

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
    PvWatts4Request request = PvWatts4Request.builder()
        .setLat(38f)
        .setLon(-118f)
        .setAzimuth(180f)
        .setTilt(10f)
        .setDataset("tmy2")
        .setSystemSize(5f)
        .setDerate(0.85f)
        .setTrackMode(1)
        .build();
    
    PvWatts4Response result = service.execute(request);
    
    assertThat(result.getOutputs().getAcAnnual(), greaterThan(0f));
  }
}
