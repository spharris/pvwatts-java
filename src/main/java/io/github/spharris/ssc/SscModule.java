package io.github.spharris.ssc;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.sun.jna.Native;

import io.github.spharris.ssc.Module.SscLibraryName;

public class SscModule extends AbstractModule {

  private static final String SSC_LIB_NAME = "ssc";

  @Override
  protected void configure() {
    bind(Ssc.class).toInstance((Ssc) Native.loadLibrary(SSC_LIB_NAME, Ssc.class));

    bind(Key.get(String.class, SscLibraryName.class)).toInstance(SSC_LIB_NAME);
    requestStaticInjection(Module.class);

    install(new FactoryModuleBuilder().implement(Module.class, Module.class)
        .build(ModuleFactory.class));
  }
}
