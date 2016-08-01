package io.github.spharris.ssc;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.google.common.base.Optional;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;

import io.github.spharris.ssc.ExecutionHandler.MessageType;
import io.github.spharris.ssc.exceptions.UnknownModuleNameException;

/**
 * A <code>Module</code> represents an SSC compute module ("pvwattsv1", for example).
 * 
 * Once a module has been created , you can get information about the available variables, add
 * values for variables, and execute.
 * 
 * <strong>When you are done with a module, it must be freed using {@link #free}.
 * 
 * @author spharris
 */
public class Module {

  @BindingAnnotation
  @Retention(RetentionPolicy.RUNTIME)
  @interface SscLibraryName {}

  private enum ActionType {
    LOG, UPDATE;

    public static ActionType forInt(int action) {
      checkArgument(action == 0 || action == 1, "action must have a value of 0 or 1");

      if (action == 0) {
        return LOG;
      } else {
        return UPDATE;
      }
    }
  }

  private static final int FLOAT_SIZE = 4;

  @SscLibraryName
  private static String SSC_LIB_NAME;

  private String moduleName;
  private Pointer module;
  private Pointer data;
  private Pointer entry;
  private Ssc api;
  private List<Variable> variables;

  private boolean freed = false;

  /**
   * Gets a list of the available modules.
   */
  public static List<ModuleSummary> getAvailableModules() {
    Ssc api = loadSscLibrary();

    List<ModuleSummary> modules = new LinkedList<>();

    int i = 0;
    Pointer entry = api.ssc_module_entry(i);
    while (entry != null) {
      String name = api.ssc_entry_name(entry);
      String desc = api.ssc_entry_description(entry);
      int version = api.ssc_entry_version(entry);

      modules.add(new ModuleSummary(name, desc, version));

      i++;
      entry = api.ssc_module_entry(i);
    }

    return modules;
  }

  private static Ssc loadSscLibrary() {
    return (Ssc) Native.loadLibrary(SSC_LIB_NAME, Ssc.class);
  }

  @Inject
  Module(@Assisted String moduleName, Ssc api) {
    module = api.ssc_module_create(moduleName);
    if (module == null) {
      throw new UnknownModuleNameException(moduleName);
    }

    this.moduleName = moduleName;
    this.api = api;
    entry = getEntry();
    data = api.ssc_data_create();
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

  public ModuleSummary getShortModuleInfo() {
    return new ModuleSummary(getName(), getDescription(), getVersion());
  }

  public ModuleSummary getModuleInfo() {
    return new ModuleSummary(getName(), getDescription(), getVersion(), getVariables());
  }

  /**
   * Get the name of SSC compute module
   */
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
   * @throws {@link java.lang.IllegalStateException} if the module has already been {@link #free}ed.
   */
  public List<Variable> getVariables() {
    checkState();

    if (variables != null) {
      return variables;
    }

    variables = new LinkedList<>();

    int i = 0;
    Pointer infoPointer = api.ssc_module_var_info(module, i);
    while (infoPointer != null) {
      Variable var = Variable.buildVariable()
          .varType(Variable.VariableType.forInt(api.ssc_info_var_type(infoPointer)))
          .dataType(Variable.DataType.forInt(api.ssc_info_data_type(infoPointer)))
          .name(api.ssc_info_name(infoPointer)).label(api.ssc_info_label(infoPointer))
          .units(api.ssc_info_units(infoPointer)).meta(api.ssc_info_meta(infoPointer))
          .group(api.ssc_info_group(infoPointer)).required(api.ssc_info_required(infoPointer))
          .build();

      variables.add(var);

      i++;
      infoPointer = api.ssc_module_var_info(module, i);
    }

    return variables;
  }

  public void execute() {
    api.ssc_module_exec(module, data);
  }

  public void execute(final ExecutionHandler handler) {

    SscExecutionHandler wrapper = new SscExecutionHandler() {

      @Override
      public boolean update(Pointer module, Pointer sscFunction, int action, float f0, float f1,
          String s0, String s1, Pointer userData) {
        ActionType type = ActionType.forInt(action);
        if (type == ActionType.LOG) {
          MessageType msg = MessageType.forInt((int) f0);
          return handler.handleLogMessage(msg, f1, s0);
        } else {
          return handler.handleProgressUpdate(f0, f1, s0);
        }
      }
    };

    api.ssc_module_exec_with_handler(module, data, wrapper, null);
  }

  /**
   * Set a numeric input value for this module.
   * 
   * @param variableName The name of the variable to set.
   * @param value The value.
   * 
   * @throws {@link java.lang.IllegalStateException} if the module has already been {@link #free}ed.
   * @throws {@link java.lang.NullPointerException} if <tt>variableName</tt> is null
   */
  public void setValue(String variableName, float value) {
    checkState();
    checkNotNull(variableName);

    api.ssc_data_set_number(data, variableName, value);
  }

  public void setValue(String variableName, int value) {
    setValue(variableName, (float) value);
  }

  public void setValue(String variableName, long value) {
    setValue(variableName, (float) value);
  }

  public void setValue(String variableName, double value) {
    setValue(variableName, (float) value);
  }

  public void setValue(String variableName, Number value) {
    setValue(variableName, value.floatValue());
  }

  /**
   * Set a String input value for this module.
   * 
   * @param variableName The name of the variable to set.
   * @param value The value.
   * 
   * @throws {@link java.lang.IllegalStateException} if the module has already been {@link #free}ed.
   * @throws {@link java.lang.NullPointerException} if <tt>variableName</tt> or <tt>value</tt> is
   *         null
   */
  public void setValue(String variableName, String value) {
    checkState();
    checkNotNull(value);

    api.ssc_data_set_string(data, variableName, value);
  }

  /**
   * Set an array input value for this module.
   * 
   * @param variableName The name of the variable to set.
   * @param value The value.
   * 
   * @throws {@link java.lang.IllegalStateException} if the module has already been {@link #free}ed.
   * @throws {@link java.lang.NullPointerException} if <tt>variableName</tt> or <tt>value</tt> is
   *         null
   * @throws {@link java.lang.IllegalArgumentException} if <tt>value</tt> has a length of zero.
   */
  public void setValue(String variableName, float[] value) {
    checkState();
    checkNotNull(value);
    checkArgument(value.length >= 1, "The length of the array must be >= 1.");

    api.ssc_data_set_array(data, variableName, value, value.length);
  }

  public void setValue(String variableName, int[] value) {
    float[] floats = new float[value.length];
    for (int i = 0; i < value.length; i++) {
      floats[i] = value[i];
    }

    setValue(variableName, floats);
  }

  public void setValue(String variableName, long[] value) {
    float[] floats = new float[value.length];
    for (int i = 0; i < value.length; i++) {
      floats[i] = value[i];
    }

    setValue(variableName, floats);
  }

  public void setValue(String variableName, double[] value) {
    float[] floats = new float[value.length];
    for (int i = 0; i < value.length; i++) {
      floats[i] = (float) value[i];
    }

    setValue(variableName, floats);
  }

  public void setValue(String variableName, Number[] value) {
    float[] floats = new float[value.length];
    for (int i = 0; i < value.length; i++) {
      floats[i] = value[i].floatValue();
    }

    setValue(variableName, floats);
  }

  /**
   * Set an array input value for this module.
   * 
   * @param variableName The name of the variable to set.
   * @param value The value.
   * 
   * @throws {@link java.lang.IllegalStateException} if the module has already been {@link #free}ed.
   * @throws {@link java.lang.NullPointerException} if <tt>variableName</tt> or <tt>value</tt> is
   *         null
   * @throws {@link java.lang.IllegalArgumentException} if <tt>value</tt> has zero rows or columns
   */
  public void setValue(String variableName, float[][] value) {
    checkState();
    checkNotNull(value);
    checkArgument(value.length >= 1, "The number of rows must be >= 1.");
    checkArgument(value[0].length >= 1, "The number of columns must be >= 1.");

    int rows = value.length;
    int cols = value[0].length;
    float[] inputArray = new float[rows * cols];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        inputArray[arrayIndex(i, j, cols)] = value[i][j];
      }
    }

    api.ssc_data_set_matrix(data, variableName, inputArray, rows, cols);
  }

  public void setValue(String variableName, int[][] value) {
    checkNotNull(value);
    checkArgument(value.length >= 1, "The number of rows must be >= 1.");
    checkArgument(value[0].length >= 1, "The number of columns must be >= 1.");

    float[][] floats = new float[value.length][value[0].length];
    for (int i = 0; i < value.length; i++) {
      for (int j = 0; j < value[i].length; i++) {
        floats[i][j] = value[i][j];
      }
    }

    setValue(variableName, floats);
  }

  public void setValue(String variableName, long[][] value) {
    checkNotNull(value);
    checkArgument(value.length >= 1, "The number of rows must be >= 1.");
    checkArgument(value[0].length >= 1, "The number of columns must be >= 1.");

    float[][] floats = new float[value.length][value[0].length];
    for (int i = 0; i < value.length; i++) {
      for (int j = 0; j < value[i].length; i++) {
        floats[i][j] = value[i][j];
      }
    }

    setValue(variableName, floats);
  }

  public void setValue(String variableName, double[][] value) {
    checkNotNull(value);
    checkArgument(value.length >= 1, "The number of rows must be >= 1.");
    checkArgument(value[0].length >= 1, "The number of columns must be >= 1.");

    float[][] floats = new float[value.length][value[0].length];
    for (int i = 0; i < value.length; i++) {
      for (int j = 0; j < value[i].length; i++) {
        floats[i][j] = (float) value[i][j];
      }
    }

    setValue(variableName, floats);
  }

  public void setValue(String variableName, Number[][] value) {
    checkNotNull(value);
    checkArgument(value.length >= 1, "The number of rows must be >= 1.");
    checkArgument(value[0].length >= 1, "The number of columns must be >= 1.");

    float[][] floats = new float[value.length][value[0].length];
    for (int i = 0; i < value.length; i++) {
      for (int j = 0; j < value[i].length; i++) {
        floats[i][j] = value[i][j].floatValue();
      }
    }

    setValue(variableName, floats);
  }

  /**
   * Get a numeric input or output number for this module.
   * 
   * @param variableName The name of the variable to retrieve.
   * @return If <tt>variableName</tt> is found, an {@link Optional} containing the value. Otherwise,
   *         an empty {@link Optional}
   * 
   * @throws {@link java.lang.IllegalStateException} if the module has already been {@link #free}ed.
   * @throws {@link java.lang.NullPointerException} if <tt>variableName</tt> is null
   */
  public Optional<Float> getNumber(String variableName) {
    checkState();
    checkNotNull(variableName);

    FloatByReference value = new FloatByReference();
    boolean present = api.ssc_data_get_number(data, variableName, value);

    if (present) {
      return Optional.of(value.getValue());
    } else {
      return Optional.absent();
    }
  }

  /**
   * Get a numeric input or output number for this module.
   * 
   * @param variableName The name of the variable to retrieve.
   * @return If <tt>variableName</tt> is found, an {@link Optional} containing the value. Otherwise,
   *         an empty {@link Optional}
   * 
   * @throws {@link java.lang.IllegalStateException} if the module has already been {@link #free}ed.
   * @throws {@link java.lang.NullPointerException} if <tt>variableName</tt> is null
   */
  public Optional<String> getString(String variableName) {
    checkState();
    checkNotNull(variableName);

    String val = api.ssc_data_get_string(data, variableName);
    return Optional.fromNullable(val);
  }

  /**
   * Get a numeric input or output number for this module.
   * 
   * @param variableName The name of the variable to retrieve.
   * @return If <tt>variableName</tt> is found, an {@link Optional} containing the value. Otherwise,
   *         an empty {@link Optional}
   * 
   * @throws {@link java.lang.IllegalStateException} if the module has already been {@link #free}ed.
   * @throws {@link java.lang.NullPointerException} if <tt>variableName</tt> is null
   */
  public Optional<float[]> getArray(String variableName) {
    checkState();
    checkNotNull(variableName);

    IntByReference length = new IntByReference();
    Pointer result = api.ssc_data_get_array(data, variableName, length);

    if (result == null) {
      return Optional.<float[]>absent();
    } else {
      int len = length.getValue();
      float[] arr = new float[len];

      for (int i = 0; i < len; i++) {
        arr[i] = result.getFloat(i * FLOAT_SIZE);
      }

      return Optional.of(arr);
    }
  }

  /**
   * Get a numeric input or output number for this module.
   * 
   * @param variableName The name of the variable to retrieve.
   * @return If <tt>variableName</tt> is found, an {@link Optional} containing the value. Otherwise,
   *         an empty {@link Optional}
   * 
   * @throws {@link java.lang.IllegalStateException} if the module has already been {@link #free}ed.
   * @throws {@link java.lang.NullPointerException} if <tt>variableName</tt> is null
   */
  public Optional<float[][]> getMatrix(String variableName) {
    checkState();
    checkNotNull(variableName);

    IntByReference rows = new IntByReference();
    IntByReference cols = new IntByReference();
    Pointer result = api.ssc_data_get_matrix(data, variableName, rows, cols);

    if (result == null) {
      return Optional.<float[][]>absent();
    } else {
      float[][] value = new float[rows.getValue()][cols.getValue()];
      for (int i = 0; i < rows.getValue(); i++) {
        for (int j = 0; j < cols.getValue(); j++) {
          value[i][j] = result.getFloat(arrayIndex(i, j, cols.getValue()) * FLOAT_SIZE);
        }
      }

      return Optional.of(value);
    }
  }

  private static int arrayIndex(int i, int j, int cols) {
    return (i * cols) + j;
  }

  /**
   * Frees the underlying pointers associated with this module. Subsequent calls to other module
   * functions will result in an {@link java.lang.IllegalStateException}
   */
  public void free() {
    if (!freed) {
      api.ssc_module_free(module);
      api.ssc_data_free(data);
      freed = true;
    }
  }

  public boolean isFreed() {
    return freed;
  }

  private void checkState() {
    if (freed) {
      throw new IllegalStateException("The module has already been freed.");
    }
  }
}
