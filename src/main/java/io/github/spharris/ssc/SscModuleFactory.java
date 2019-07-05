package io.github.spharris.ssc;

/** Factory interface for creating {@link SscModule SscModules}. */
public interface SscModuleFactory {
  SscModule create(String moduleName);
}
