package io.github.spharris.ssc.utils;

import io.github.spharris.ssc.Module;

/**
 * An object representing an input variable to an SSC module.
 */
public interface InputVariable<T> extends SscVariable {
  
  /**
   * Set <code>value</code> in <code>module</code>. If <code>value</code> is <code>null</code>,
   * this method has no effect.
   */
  void set(T value, Module module);
}
