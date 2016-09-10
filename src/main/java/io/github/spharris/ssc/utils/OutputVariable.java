package io.github.spharris.ssc.utils;

import io.github.spharris.ssc.Module;

public interface OutputVariable<T> extends SscVariable {
  T get(Module module);
}
