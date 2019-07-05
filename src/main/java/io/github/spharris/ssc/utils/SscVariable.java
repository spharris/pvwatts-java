package io.github.spharris.ssc.utils;

import io.github.spharris.ssc.DataContainer;

/** A generic SSC variable */
public interface SscVariable<T> {
  String getName();

  T get(DataContainer data);
}
