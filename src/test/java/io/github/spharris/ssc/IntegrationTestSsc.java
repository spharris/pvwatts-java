package io.github.spharris.ssc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;

@RunWith(JUnit4.class)
public class IntegrationTestSsc {

  static final float EPSILON = 0f;
  
  private Ssc ssclib;
  private Pointer data;

  @Before
  public void loadLibrary() {
    ssclib = (Ssc) Native.loadLibrary("ssc", Ssc.class);
  }

  @Before
  public void prepareData() {
    data = ssclib.ssc_data_create();
  }

  @After
  public void cleanupData() {
    ssclib.ssc_data_free(data);
  }

  @Test
  public void basicInfoPopulatesCorrectly() {
    assertThat(ssclib.ssc_build_info()).isNotNull();
    assertThat(ssclib.ssc_version()).isGreaterThan(0);
  }

  @Test
  public void dataIsLoaded() {
    assertThat(data).isNotNull();
  }

  @Test
  public void setNumber() {
    float targetVal = 0.5f;
    String name = "test";

    ssclib.ssc_data_set_number(data, name, targetVal);
    FloatByReference out = new FloatByReference();
    boolean status = ssclib.ssc_data_get_number(data, name, out);

    assertThat(status).isTrue();
    assertThat(out.getValue()).isWithin(EPSILON).of(targetVal);
  }

  @Test
  public void setString() {
    String targetString = "test string";
    String name = "test";

    ssclib.ssc_data_set_string(data, name, targetString);
    String result = ssclib.ssc_data_get_string(data, name);

    assertThat(result).isEqualTo(targetString);
  }

  @Test
  public void setArray() {
    float value = 0.5f;
    float[] array = {value, value, value, value};
    String name = "test";

    ssclib.ssc_data_set_array(data, name, array, array.length);
    IntByReference length = new IntByReference();
    Pointer result = ssclib.ssc_data_get_array(data, name, length);

    assertThat(length.getValue()).isEqualTo(array.length);
    for (int i = 0; i < length.getValue(); i++) {
      // Offset is by byte (float = 4 bytes)
      assertThat(result.getFloat(i * 4)).isWithin(EPSILON).of(value);
    }
  }

  @Test
  public void setMatrix() {
    float value = 0.5f;
    int n = 2;
    float[] array = {value, value, value, value};
    String name = "test";

    ssclib.ssc_data_set_matrix(data, name, array, n, n);
    IntByReference rows = new IntByReference();
    IntByReference cols = new IntByReference();
    Pointer result = ssclib.ssc_data_get_matrix(data, name, rows, cols);

    assertThat(rows.getValue()).isEqualTo(n);
    assertThat(cols.getValue()).isEqualTo(n);
    for (int i = 0; i < rows.getValue(); i++) {
      for (int j = 0; j < cols.getValue(); j++) {
        // Offset is by byte (float = 4 bytes)
        assertThat(result.getFloat((i * j + j) * 4)).isWithin(EPSILON).of(value);
      }
    }
  }

  @Test
  public void setTable() {
    Pointer table = ssclib.ssc_data_create();
    String name = "test";
    String val = "test string";

    ssclib.ssc_data_set_string(table, name, val);
    ssclib.ssc_data_set_table(data, name, table);

    Pointer resultTable = ssclib.ssc_data_get_table(data, name);
    String resultString = ssclib.ssc_data_get_string(resultTable, name);

    assertThat(resultString).isEqualTo(val);
  }

  @Test
  public void dataClear() {
    String name = "test";
    String targetString = "test string";

    ssclib.ssc_data_set_string(data, name, targetString);
    ssclib.ssc_data_clear(data);

    String resultString = ssclib.ssc_data_get_string(data, name);
    assertThat(resultString).isNull();
  }

  @Test
  public void dataQuery() {
    String name = "test";
    String targetString = "test string";

    ssclib.ssc_data_set_string(data, name, targetString);
    int type = ssclib.ssc_data_query(data, name);

    assertThat(type).isEqualTo(1);
  }

  @Test
  public void dataFirstWithNoDataReturnsNull() {
    assertThat(ssclib.ssc_data_first(data)).isNull();
  }

  @Test
  public void dataFirstWithData() {
    String name = "test";
    String targetString = "test string";

    ssclib.ssc_data_set_string(data, name, targetString);
    String result = ssclib.ssc_data_first(data);

    assertThat(result).isEqualTo(name);
  }

  @Test
  public void entryData() {
    int i = 0;
    Pointer entry = ssclib.ssc_module_entry(i);
    do {
      assertThat(entry).isNotNull();

      String name = ssclib.ssc_entry_name(entry);
      assertThat(name).isNotNull();

      String description = ssclib.ssc_entry_description(entry);
      assertThat(description).isNotNull();

      int version = ssclib.ssc_entry_version(entry);
      assertThat(version).isAtLeast(0);

      i++;
      entry = ssclib.ssc_module_entry(i);
    } while (entry != null);
  }

  @Test
  public void unknownModuleReturnsNull() {
    Pointer module = ssclib.ssc_module_create("unknown");
    assertThat(module).isNull();
  }

  @Test
  public void createAndFreeModule() {
    Pointer module = ssclib.ssc_module_create("pvwattsv1");
    assertThat(module).isNotNull();

    ssclib.ssc_module_free(module);
  }

  @Test
  public void getModuleInfo() {
    Pointer module = ssclib.ssc_module_create("pvwattsv1");

    int i = 0;
    Pointer info = ssclib.ssc_module_var_info(module, i);
    do {
      assertThat(info).isNotNull();

      ssclib.ssc_info_name(info);
      assertThat(ssclib.ssc_info_var_type(info)).isAtLeast(0);
      assertThat(ssclib.ssc_info_data_type(info)).isAtLeast(0);
      assertThat(ssclib.ssc_info_name(info)).isNotNull();
      assertThat(ssclib.ssc_info_label(info)).isNotNull();
      assertThat(ssclib.ssc_info_units(info)).isNotNull();
      assertThat(ssclib.ssc_info_meta(info)).isNotNull();
      assertThat(ssclib.ssc_info_group(info)).isNotNull();
      assertThat(ssclib.ssc_info_required(info)).isNotNull();
      assertThat(ssclib.ssc_info_constraints(info)).isNotNull();

      i++;
      info = ssclib.ssc_module_var_info(module, i);
    } while (info != null);

    ssclib.ssc_module_free(module);
  }

  @Test
  public void moduleExecSimple() {
    // Arrange
    initializeSimulationData(ssclib, data);

    // Act
    boolean result = ssclib.ssc_module_exec_simple("layoutarea", data);

    // Assert
    assertThat(result).isTrue();
    checkSimulationData(ssclib, data);
  }

  @Test
  public void moduleExecNoThread() {
    initializeSimulationData(ssclib, data);

    String result = ssclib.ssc_module_exec_simple_nothread("layoutarea", data);

    assertThat(result).isNull();
    checkSimulationData(ssclib, data);
  }

  @Test
  public void moduleExec() {
    initializeSimulationData(ssclib, data);

    Pointer module = ssclib.ssc_module_create("layoutarea");
    boolean result = ssclib.ssc_module_exec(module, data);

    assertThat(result).isTrue();
    checkSimulationData(ssclib, data);
    ssclib.ssc_module_free(module);
  }

  @Test
  public void testModuleLog() {
    Pointer module = ssclib.ssc_module_create("layoutarea");
    boolean result = ssclib.ssc_module_exec(module, data);

    assertThat(result).isFalse();

    int i = 0;
    IntByReference itemType = new IntByReference();
    FloatByReference time = new FloatByReference();
    String logMsg = ssclib.ssc_module_log(module, i, itemType, time);
    do {
      assertThat(logMsg).isNotNull();

      assertThat(itemType.getValue()).isGreaterThan(0);
      assertThat(time.getValue()).isNotWithin(EPSILON).of(0f);

      i++;
      logMsg = ssclib.ssc_module_log(module, i, itemType, time);
    } while (logMsg != null);
  }

  public static class TestHandler implements SscExecutionHandler {

    @Override
    public boolean update(Pointer module, Pointer handler, int action, float f0, float f1,
        String s0, String s1, Pointer userData) {

      assertThat(f0).isAtLeast(0f);

      return true;
    }
  }

  @Test
  public void testWithHandler() {
    initializeSimulationData(ssclib, data);
    Pointer module = ssclib.ssc_module_create("layoutarea");

    SscExecutionHandler handler = new TestHandler();
    boolean result = ssclib.ssc_module_exec_with_handler(module, data, handler, null);

    assertThat(result).isTrue();
    ssclib.ssc_module_free(module);
  }

  /**
   * Private helper that initializes some data for a simulation. Assumes that we want to use the
   * "layoutarea" module, as it's the simplest.
   */
  private static void initializeSimulationData(Ssc ssclib, Pointer data) {
    float[] positions = {0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f};
    ssclib.ssc_data_set_matrix(data, "positions", positions, 3, 2);
  }

  /**
   * Private helper to make sure that the data created by the simulation was set as expected.
   * Assumes that we used the "layoutarea" module
   */
  private static void checkSimulationData(Ssc ssclib, Pointer data) {
    FloatByReference val = new FloatByReference();
    boolean status = ssclib.ssc_data_get_number(data, "area", val);
    assertThat(status).isTrue();
    assertThat(val.getValue()).isAtLeast(0f);

    IntByReference rows = new IntByReference();
    IntByReference cols = new IntByReference();
    Pointer mtx = ssclib.ssc_data_get_matrix(data, "convex_hull", rows, cols);
    assertThat(mtx).isNotNull();
    assertThat(rows.getValue()).isGreaterThan(0);
    assertThat(cols.getValue()).isGreaterThan(0);
  }
}
