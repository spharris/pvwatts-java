package io.github.spharris.ssc;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import io.github.spharris.ssc.ExecutionHandler.MessageType;
import io.github.spharris.ssc.exceptions.UnknownModuleNameException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * A <code>Module</code> represents an SSC compute module ("pvwattsv1", for example). This class is
 * created via an injected {@link SscModuleFactory}.
 *
 * <p>Once a module has been created, you can get information about the available variables, add
 * values for variables, and execute.
 */
public final class SscModule implements AutoCloseable {

  private String moduleName;
  private Pointer module;
  private Pointer entry;
  private Ssc api;
  private List<Variable> variables;

  private boolean closed = false;

  /** Gets a list of the available modules. */
  public static ImmutableList<SscModuleSummary> getAvailableModules() {
    Ssc api = loadSscLibrary();

    ImmutableList.Builder<SscModuleSummary> builder = ImmutableList.builder();

    int i = 0;
    Pointer entry = api.ssc_module_entry(i);
    while (entry != null) {
      String name = api.ssc_entry_name(entry);
      String desc = api.ssc_entry_description(entry);
      int version = api.ssc_entry_version(entry);

      builder.add(
          SscModuleSummary.builder()
              .setName(name)
              .setDescription(desc)
              .setVersion(version)
              .build());

      i++;
      entry = api.ssc_module_entry(i);
    }

    return builder.build();
  }

  private static Ssc loadSscLibrary() {
    return (Ssc) Native.loadLibrary(Ssc.SSC_LIB_NAME, Ssc.class);
  }

  @Inject
  SscModule(@Assisted String moduleName, Ssc api) {
    this.module = api.ssc_module_create(moduleName);
    if (module == null) {
      throw new UnknownModuleNameException(moduleName);
    }

    this.moduleName = moduleName;
    this.api = api;
    this.entry = getEntry();
  }

  /**
   * Helper to get the SSC entry for this module. Generally you should only use this once you're
   * sure that the module in question exists.
   */
  private Pointer getEntry() {
    int i = 0;
    Pointer entry = api.ssc_module_entry(i);
    while (entry != null) {
      String name = api.ssc_entry_name(entry);
      if (Objects.equals(name, getName())) {
        return entry;
      }

      i++;
      entry = api.ssc_module_entry(i);
    }

    // Should never get here.
    throw new IllegalStateException("You cannot call getEntry for a module that doesn't exist.");
  }

  public int getSscVersion() {
    return api.ssc_version();
  }

  public String getSscBuildInfo() {
    return api.ssc_build_info();
  }

  public SscModuleSummary getModuleSummary() {
    return SscModuleSummary.builder()
        .setName(getName())
        .setDescription(getDescription())
        .setVersion(getVersion())
        .setVariables(getVariables())
        .build();
  }

  /** Get the name of SSC compute module */
  public String getName() {
    return moduleName;
  }

  public String getDescription() {
    return api.ssc_entry_description(entry);
  }

  public int getVersion() {
    return api.ssc_entry_version(entry);
  }

  /**
   * Returns a list of all of the variables for this module.
   *
   * @throws java.lang.IllegalStateException if the module has already been {@link #free}ed.
   */
  public List<Variable> getVariables() {
    checkNotClosed();

    if (variables != null) {
      return variables;
    }

    variables = new LinkedList<>();

    int i = 0;
    Pointer infoPointer = api.ssc_module_var_info(module, i);
    while (infoPointer != null) {
      Variable var =
          Variable.builder()
              .setVariableType(Variable.VariableType.forInt(api.ssc_info_var_type(infoPointer)))
              .setDataType(Variable.DataType.forInt(api.ssc_info_data_type(infoPointer)))
              .setName(api.ssc_info_name(infoPointer))
              .setLabel(api.ssc_info_label(infoPointer))
              .setUnits(api.ssc_info_units(infoPointer))
              .setMeta(api.ssc_info_meta(infoPointer))
              .setGroup(api.ssc_info_group(infoPointer))
              .setRequired(api.ssc_info_required(infoPointer))
              .build();

      variables.add(var);

      i++;
      infoPointer = api.ssc_module_var_info(module, i);
    }

    return variables;
  }

  public void execute(DataContainer data) {
    checkNotClosed();
    api.ssc_module_exec(module, data.getPointer());
  }

  public void execute(DataContainer data, final ExecutionHandler handler) {
    checkNotClosed();
    SscExecutionHandler wrapper =
        new SscExecutionHandler() {

          @Override
          public boolean update(
              Pointer module,
              Pointer sscFunction,
              int action,
              float f0,
              float f1,
              String s0,
              String s1,
              Pointer userData) {
            ActionType type = ActionType.forInt(action);
            if (type == ActionType.LOG) {
              MessageType msg = MessageType.forInt((int) f0);
              return handler.handleLogMessage(msg, f1, s0);
            } else {
              return handler.handleProgressUpdate(f0, f1, s0);
            }
          }
        };

    api.ssc_module_exec_with_handler(module, data.getPointer(), wrapper, null);
  }

  private enum ActionType {
    LOG,
    UPDATE;

    public static ActionType forInt(int action) {
      checkArgument(action == 0 || action == 1, "action must have a value of 0 or 1");

      if (action == 0) {
        return LOG;
      } else {
        return UPDATE;
      }
    }
  }

  @Override
  public void close() {
    if (!closed) {
      api.ssc_module_free(module);
      closed = true;
    }
  }

  private void checkNotClosed() {
    checkState(!closed, "This module has already been closed.");
  }
}
