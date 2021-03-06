package io.github.spharris.ssc;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import javax.annotation.Nullable;

/** A summary of a particular SSC compute module. */
@AutoValue
public abstract class SscModuleSummary {

  SscModuleSummary() {}

  public abstract String getName();

  public abstract String getDescription();

  public abstract int getVersion();

  public abstract @Nullable ImmutableList<Variable> getVariables();

  public static Builder builder() {
    return new AutoValue_SscModuleSummary.Builder();
  }

  @AutoValue.Builder
  abstract static class Builder {

    abstract Builder setName(String name);

    abstract Builder setDescription(String description);

    abstract Builder setVersion(int version);

    abstract Builder setVariables(Iterable<Variable> vars);

    public abstract SscModuleSummary build();
  }
}
