package io.github.spharris.ssc;

import java.util.function.BiFunction;
import javax.inject.Inject;
import javax.inject.Provider;

/** Class that runs an SSC simulation. */
public class SimulationRunner {

  private final SscModuleFactory moduleFactory;
  private final Provider<DataContainer> dataContainerProvider;

  @Inject
  SimulationRunner(SscModuleFactory moduleFactory,
      Provider<DataContainer> dataContainerProvider) {
    this.moduleFactory = moduleFactory;
    this.dataContainerProvider = dataContainerProvider;
  }

  public <T> T run(String moduleName, BiFunction<SscModule, DataContainer, T> transform) {
    try (SscModule module = moduleFactory.create(moduleName)) {
      try (DataContainer data = dataContainerProvider.get()) {
        return transform.apply(module, data);
      }
    }
  }
}
