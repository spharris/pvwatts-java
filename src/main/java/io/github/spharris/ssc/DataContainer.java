package io.github.spharris.ssc;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

import javax.inject.Inject;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;

/** A container for data that is passed to an {@link SscModule} for execution */
public class DataContainer implements Freeable {

  private static final int FLOAT_SIZE = 4;

  private final Ssc api;
  private final Pointer data;

  private boolean freed;

  @Inject
  DataContainer(Ssc api) {
    this.api = checkNotNull(api);

    data = api.ssc_data_create();
    freed = false;
  }

  /**
   * Get the underlying SSC data pointer. Package private because it should only be accessible from
   * {@link SscModule}.
   */
  Pointer getPointer() {
    checkState();
    return data;
  }

  /**
   * Set a numeric input value for this module.
   *
   * @param variableName The name of the variable to set.
   * @param value The value.
   * @throws {@link java.lang.IllegalStateException} if the module has already been {@link #free}ed.
   * @throws {@link java.lang.NullPointerException} if <tt>variableName</tt> is null
   */
  public DataContainer setNumber(String variableName, float value) {
    checkState();
    checkNotNull(variableName);

    api.ssc_data_set_number(data, variableName, value);
    return this;
  }

  public DataContainer setNumber(String variableName, int value) {
    return setNumber(variableName, (float) value);
  }

  public DataContainer setNumber(String variableName, long value) {
    return setNumber(variableName, (float) value);
  }

  public DataContainer setNumber(String variableName, double value) {
    return setNumber(variableName, (float) value);
  }

  public DataContainer setNumber(String variableName, Number value) {
    return setNumber(variableName, value.floatValue());
  }

  /**
   * Set a String input value for this module.
   *
   * @param variableName The name of the variable to set.
   * @param value The value.
   * @throws {@link java.lang.IllegalStateException} if the module has already been {@link #free}ed.
   * @throws {@link java.lang.NullPointerException} if <tt>variableName</tt> or <tt>value</tt> is
   *     null
   */
  public DataContainer setString(String variableName, String value) {
    checkState();
    checkNotNull(value);

    api.ssc_data_set_string(data, variableName, value);
    return this;
  }

  /**
   * Set an array input value for this module.
   *
   * @param variableName The name of the variable to set.
   * @param value The value.
   * @throws {@link java.lang.IllegalStateException} if the module has already been {@link #free}ed.
   * @throws {@link java.lang.NullPointerException} if <tt>variableName</tt> or <tt>value</tt> is
   *     null
   * @throws {@link java.lang.IllegalArgumentException} if <tt>value</tt> has a length of zero.
   */
  public DataContainer setArray(String variableName, float[] value) {
    checkState();
    checkNotNull(value);
    checkArgument(value.length >= 1, "The length of the array must be >= 1.");

    api.ssc_data_set_array(data, variableName, value, value.length);
    return this;
  }

  public DataContainer setArray(String variableName, int[] value) {
    float[] floats = new float[value.length];
    for (int i = 0; i < value.length; i++) {
      floats[i] = value[i];
    }

    return setArray(variableName, floats);
  }

  public DataContainer setArray(String variableName, long[] value) {
    float[] floats = new float[value.length];
    for (int i = 0; i < value.length; i++) {
      floats[i] = value[i];
    }

    return setArray(variableName, floats);
  }

  public DataContainer setArray(String variableName, double[] value) {
    float[] floats = new float[value.length];
    for (int i = 0; i < value.length; i++) {
      floats[i] = (float) value[i];
    }

    return setArray(variableName, floats);
  }

  public DataContainer setArray(String variableName, Number[] value) {
    float[] floats = new float[value.length];
    for (int i = 0; i < value.length; i++) {
      floats[i] = value[i].floatValue();
    }

    return setArray(variableName, floats);
  }

  /**
   * Set an array input value for this module.
   *
   * @param variableName The name of the variable to set.
   * @param value The value.
   * @throws {@link java.lang.IllegalStateException} if the module has already been {@link #free}ed.
   * @throws {@link java.lang.NullPointerException} if <tt>variableName</tt> or <tt>value</tt> is
   *     null
   * @throws {@link java.lang.IllegalArgumentException} if <tt>value</tt> has zero rows or columns
   */
  public DataContainer setMatrix(String variableName, float[][] value) {
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
    return this;
  }

  public DataContainer setMatrix(String variableName, int[][] value) {
    checkNotNull(value);
    checkArgument(value.length >= 1, "The number of rows must be >= 1.");
    checkArgument(value[0].length >= 1, "The number of columns must be >= 1.");

    float[][] floats = new float[value.length][value[0].length];
    for (int i = 0; i < value.length; i++) {
      for (int j = 0; j < value[i].length; i++) {
        floats[i][j] = value[i][j];
      }
    }

    return setMatrix(variableName, floats);
  }

  public DataContainer setMatrix(String variableName, long[][] value) {
    checkNotNull(value);
    checkArgument(value.length >= 1, "The number of rows must be >= 1.");
    checkArgument(value[0].length >= 1, "The number of columns must be >= 1.");

    float[][] floats = new float[value.length][value[0].length];
    for (int i = 0; i < value.length; i++) {
      for (int j = 0; j < value[i].length; i++) {
        floats[i][j] = value[i][j];
      }
    }

    return setMatrix(variableName, floats);
  }

  public DataContainer setMatrix(String variableName, double[][] value) {
    checkNotNull(value);
    checkArgument(value.length >= 1, "The number of rows must be >= 1.");
    checkArgument(value[0].length >= 1, "The number of columns must be >= 1.");

    float[][] floats = new float[value.length][value[0].length];
    for (int i = 0; i < value.length; i++) {
      for (int j = 0; j < value[i].length; i++) {
        floats[i][j] = (float) value[i][j];
      }
    }

    return setMatrix(variableName, floats);
  }

  public DataContainer setMatrix(String variableName, Number[][] value) {
    checkNotNull(value);
    checkArgument(value.length >= 1, "The number of rows must be >= 1.");
    checkArgument(value[0].length >= 1, "The number of columns must be >= 1.");

    float[][] floats = new float[value.length][value[0].length];
    for (int i = 0; i < value.length; i++) {
      for (int j = 0; j < value[i].length; i++) {
        floats[i][j] = value[i][j].floatValue();
      }
    }

    return setMatrix(variableName, floats);
  }

  /**
   * Get a numeric input or output number for this module.
   *
   * @param variableName The name of the variable to retrieve.
   * @return If <tt>variableName</tt> is found, an {@link Optional} containing the value. Otherwise,
   *     an empty {@link Optional}
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
      return Optional.empty();
    }
  }

  /**
   * Get a numeric input or output number for this module.
   *
   * @param variableName The name of the variable to retrieve.
   * @return If <tt>variableName</tt> is found, an {@link Optional} containing the value. Otherwise,
   *     an empty {@link Optional}
   * @throws {@link java.lang.IllegalStateException} if the module has already been {@link #free}ed.
   * @throws {@link java.lang.NullPointerException} if <tt>variableName</tt> is null
   */
  public Optional<String> getString(String variableName) {
    checkState();
    checkNotNull(variableName);

    String val = api.ssc_data_get_string(data, variableName);
    return Optional.ofNullable(val);
  }

  /**
   * Get a numeric input or output number for this module.
   *
   * @param variableName The name of the variable to retrieve.
   * @return If <tt>variableName</tt> is found, an {@link Optional} containing the value. Otherwise,
   *     an empty {@link Optional}
   * @throws {@link java.lang.IllegalStateException} if the module has already been {@link #free}ed.
   * @throws {@link java.lang.NullPointerException} if <tt>variableName</tt> is null
   */
  public Optional<float[]> getArray(String variableName) {
    checkState();
    checkNotNull(variableName);

    IntByReference length = new IntByReference();
    Pointer result = api.ssc_data_get_array(data, variableName, length);

    if (result == null) {
      return Optional.<float[]>empty();
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
   *     an empty {@link Optional}
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
      return Optional.<float[][]>empty();
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

  @Override
  public void free() {
    if (!freed) {
      api.ssc_data_free(data);
      freed = true;
    }
  }

  @Override
  public boolean isFreed() {
    return freed;
  }

  private void checkState() {
    if (freed) {
      throw new IllegalStateException("This data container has already been freed.");
    }
  }
}
