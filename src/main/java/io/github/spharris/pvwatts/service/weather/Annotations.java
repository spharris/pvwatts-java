package io.github.spharris.pvwatts.service.weather;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

public final class Annotations {
  
  private Annotations() {}
  
  @Qualifier
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Tmy2 {}
}
