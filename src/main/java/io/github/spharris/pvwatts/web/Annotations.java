package io.github.spharris.pvwatts.web;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Qualifier;

public final class Annotations {

  @Qualifier
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Port {}
}
