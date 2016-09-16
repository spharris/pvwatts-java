package io.github.spharris.ssc;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SscModuleSummary {

  private final String name;
  private final String description;
  private final int version;
  private final List<Variable> variables;
  private String url;

  SscModuleSummary(String name, String description, int version) {
    this.name = name;
    this.description = description;
    this.version = version;
    this.variables = null;
  }

  SscModuleSummary(String name, String description, int version, List<Variable> vars) {
    this.name = name;
    this.description = description;
    this.version = version;
    this.variables = vars;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public int getVersion() {
    return version;
  }

  public List<Variable> getVariables() {
    return variables;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
