package io.github.spharris.ssc.utils;

import io.github.spharris.ssc.DataContainer;

/**
 * An object representing an input variable to an SSC module.
 */
public interface InputVariable<T> extends SscVariable<T> {
  
  /**
   * Set <code>value</code> in <code>data</code>. If <code>value</code> is <code>null</code>,
   * this method has no effect.
   */
  void set(T value, DataContainer data);
}
