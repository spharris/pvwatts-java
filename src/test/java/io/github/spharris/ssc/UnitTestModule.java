package io.github.spharris.ssc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.base.Optional;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;

import io.github.spharris.ssc.exceptions.UnknownModuleNameException;

public class UnitTestModule {

  private static final String MODULE_NAME = "test module";

  private Ssc mockApi;

  @Before
  public void createMockApi() {
    mockApi = mock(Ssc.class);
  }

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void throwsExceptiononUnknownModule() {
    when(mockApi.ssc_module_create(anyString())).thenReturn(null);

    thrown.expect(UnknownModuleNameException.class);

    new Module("asdkfsd", mockApi);
  }

  @Test
  public void setNumber() {
    Module m = getRealModule();

    String varName = "var";
    float value = 0.5f;
    m.setValue(varName, value);

    verify(mockApi).ssc_data_set_number(any(Pointer.class), eq(varName), eq(value));
  }

  @Test
  public void getNonExistentNumber() {
    Module m = getRealModule();
    when(mockApi.ssc_data_get_number(any(Pointer.class), any(String.class),
        any(FloatByReference.class))).thenReturn(false);

    Optional<Float> val = m.getNumber("asdf");
    assertThat(val.isPresent(), equalTo(false));
  }

  @Test
  public void setString() {
    Module m = getRealModule();

    String varName = "var";
    float value = 0.5f;
    m.setValue(varName, value);

    verify(mockApi).ssc_data_set_number(any(Pointer.class), eq(varName), eq(value));
  }

  @Test
  public void getNonExistentString() {
    Module m = getRealModule();
    when(mockApi.ssc_data_get_string(any(Pointer.class), any(String.class))).thenReturn(null);

    Optional<String> val = m.getString("asdf");
    assertThat(val.isPresent(), equalTo(false));
  }

  @Test
  public void getString() {
    Module m = getRealModule();
    String returnVal = "Hello, world!";
    when(mockApi.ssc_data_get_string(any(Pointer.class), any(String.class))).thenReturn(returnVal);

    Optional<String> val = m.getString("asdf");
    assertThat(val.isPresent(), equalTo(true));
    assertThat(val.get(), equalTo(returnVal));
  }

  @Test
  public void setNumberThrowsErrorOnFreedModule() {
    Module m = getFreedModule();

    thrown.expect(IllegalStateException.class);

    m.setValue("var", 0f);
  }

  @Test
  public void freeCallsSccFree() {
    Module m = getRealModule();

    m.free();

    verify(mockApi).ssc_module_free(any(Pointer.class));
  }

  @Test
  public void multipleFreeCallsOnlyFreesOnce() {
    Module m = getRealModule();

    m.free();
    m.free();

    verify(mockApi, times(1)).ssc_module_free(any(Pointer.class));
  }

  @Test
  public void arrayLengthGreaterThanZero() {
    Module m = getRealModule();

    float[] input = new float[0];
    thrown.expect(IllegalArgumentException.class);

    m.setValue("asdf", input);
  }

  @Test
  public void mtxRowsGreaterThanZero() {
    Module m = getRealModule();

    float[][] input = new float[0][1];
    thrown.expect(IllegalArgumentException.class);

    m.setValue("asdf", input);
  }

  @Test
  public void mtxColsGreaterThanZero() {
    Module m = getRealModule();

    float[][] input = new float[1][0];
    thrown.expect(IllegalArgumentException.class);

    m.setValue("asdf", input);
  }

  @Test
  public void callsSetMatrixProperly() {
    Module m = getRealModule();

    float[][] value = {{1, 2}, {3, 4}};

    String varName = "adsf";
    m.setValue(varName, value);

    float[] expected = {1, 2, 3, 4};
    verify(mockApi).ssc_data_set_matrix(any(Pointer.class), eq(varName), eq(expected), eq(2),
        eq(2));
  }

  @Test
  public void callsSetMatrixProperlyMoreRows() {
    Module m = getRealModule();

    float[][] value = {{1, 2}, {3, 4}, {5, 6}};

    String varName = "adsf";
    m.setValue(varName, value);

    float[] expected = {1, 2, 3, 4, 5, 6};
    verify(mockApi).ssc_data_set_matrix(any(Pointer.class), eq(varName), eq(expected), eq(3),
        eq(2));
  }

  @Test
  public void getVariableCallsAllInfoFunctions() {
    Module m = getRealModule();

    Pointer p = mock(Pointer.class);
    when(mockApi.ssc_module_var_info(any(Pointer.class), eq(0))).thenReturn(p);

    m.getVariables();

    verify(mockApi, times(2)).ssc_module_var_info(any(Pointer.class), any(Integer.class));
    verify(mockApi).ssc_info_var_type(any(Pointer.class));
    verify(mockApi).ssc_info_data_type(any(Pointer.class));
    verify(mockApi).ssc_info_name(any(Pointer.class));
    verify(mockApi).ssc_info_label(any(Pointer.class));
    verify(mockApi).ssc_info_units(any(Pointer.class));
    verify(mockApi).ssc_info_meta(any(Pointer.class));
    verify(mockApi).ssc_info_group(any(Pointer.class));
    verify(mockApi).ssc_info_required(any(Pointer.class));
  }

  @Test
  public void getsVariableData() {
    Module m = getRealModule();

    Pointer p = mock(Pointer.class);
    when(mockApi.ssc_module_var_info(any(Pointer.class), eq(0))).thenReturn(p);

    when(mockApi.ssc_info_var_type(any(Pointer.class))).thenReturn(1);
    when(mockApi.ssc_info_data_type(any(Pointer.class))).thenReturn(1);
    when(mockApi.ssc_info_name(any(Pointer.class))).thenReturn("Name");
    when(mockApi.ssc_info_label(any(Pointer.class))).thenReturn("Label");
    when(mockApi.ssc_info_units(any(Pointer.class))).thenReturn("Units");
    when(mockApi.ssc_info_meta(any(Pointer.class))).thenReturn("Meta");
    when(mockApi.ssc_info_group(any(Pointer.class))).thenReturn("Group");
    when(mockApi.ssc_info_required(any(Pointer.class))).thenReturn("Required");

    List<Variable> vars = m.getVariables();

    Variable expected = Variable.buildVariable().varType(Variable.VariableType.forInt(1))
        .dataType(Variable.DataType.forInt(1)).name("Name").label("Label").units("Units")
        .meta("Meta").group("Group").required("Required").build();

    assertThat(vars, hasSize(1));
    assertThat(vars.get(0), equalTo(expected));
  }

  private Module getRealModule() {
    Pointer fakePointer = mock(Pointer.class);
    when(mockApi.ssc_module_create(anyString())).thenReturn(fakePointer);
    when(mockApi.ssc_module_entry(isA(Integer.class))).thenReturn(fakePointer);
    when(mockApi.ssc_entry_name(isA(Pointer.class))).thenReturn(MODULE_NAME);

    Module m = new Module(MODULE_NAME, mockApi);
    return m;
  }

  private Module getFreedModule() {
    Module m = getRealModule();
    m.free();

    return m;
  }
}
