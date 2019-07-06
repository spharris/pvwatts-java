package io.github.spharris.ssc;

import static com.google.common.truth.Truth.assertThat;

import com.google.inject.Guice;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SimulationRunnerTest {

  @Inject
  private SimulationRunner runner;

  @Before
  public void createInjector() {
    Guice.createInjector(new SscGuiceModule()).injectMembers(this);
  }

  @Test
  public void runsSimulation() {
    float area = runner.run("layoutarea", (module, data) -> {
      data.setMatrix("positions", new float[][] {{1f, 1f}, {1f, 1f}});
      module.execute(data);
      return data.getNumber("area").orElseThrow(IllegalStateException::new);
    });

    assertThat(area).isFinite();
  }
}
