package io.github.spharris.pvwatts.service.v4;

import javax.inject.Inject;

import io.github.spharris.ssc.ModuleFactory;

public final class PvWatts4Service {
  
  private final ModuleFactory moduleFactory;
  
  @Inject
  public PvWatts4Service(ModuleFactory moduleFactory) {
    this.moduleFactory = moduleFactory;
  }
}
