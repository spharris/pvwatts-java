package io.github.spharris.ssc.exceptions;

/**
 * Thrown when a user tries to create a new {@link io.github.spharris.ssc.SscModule} with a module name
 * that doesn't exist
 * 
 * @author spharris
 */
public final class UnknownModuleNameException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private String moduleName;

  public UnknownModuleNameException(String name) {
    this.moduleName = name;
  }

  public String getModuleName() {
    return moduleName;
  }
}
