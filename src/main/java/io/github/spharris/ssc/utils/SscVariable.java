package io.github.spharris.ssc.utils;

import io.github.spharris.ssc.Module;

/**
 * A generic SSC variable
 */
public interface SscVariable<T> {
  String getName();
  T get(Module module);
}
