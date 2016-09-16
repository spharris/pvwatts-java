package io.github.spharris.ssc.utils;

import io.github.spharris.ssc.SscModule;

/**
 * A generic SSC variable
 */
public interface SscVariable<T> {
  String getName();
  T get(SscModule module);
}
