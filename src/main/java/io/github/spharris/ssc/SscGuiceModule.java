package io.github.spharris.ssc;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.sun.jna.Native;

public class SscGuiceModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(Ssc.class).toInstance((Ssc) Native.loadLibrary(Ssc.SSC_LIB_NAME, Ssc.class));
    bind(DataContainer.class);

    install(new FactoryModuleBuilder().implement(SscModule.class, SscModule.class)
        .build(SscModuleFactory.class));
  }
}
