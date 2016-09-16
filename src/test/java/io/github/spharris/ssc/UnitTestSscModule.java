package io.github.spharris.ssc;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sun.jna.Pointer;

import io.github.spharris.ssc.exceptions.UnknownModuleNameException;

@RunWith(MockitoJUnitRunner.class)
public class UnitTestSscModule {

  private static final String MODULE_NAME = "test module";

  @Mock private Ssc mockApi;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void throwsExceptiononUnknownModule() {
    when(mockApi.ssc_module_create(anyString())).thenReturn(null);

    thrown.expect(UnknownModuleNameException.class);

    new SscModule("asdkfsd", mockApi);
  }

  @Test
  public void freeCallsSccFree() {
    SscModule m = getRealModule();

    m.free();

    verify(mockApi).ssc_module_free(any(Pointer.class));
  }

  @Test
  public void multipleFreeCallsOnlyFreesOnce() {
    SscModule m = getRealModule();

    m.free();
    m.free();

    verify(mockApi, times(1)).ssc_module_free(any(Pointer.class));
  }

  @Test
  public void getsVariableData() {
    SscModule m = getRealModule();

    Pointer p = mock(Pointer.class);
    when(mockApi.ssc_module_var_info(any(Pointer.class), eq(0))).thenReturn(p);

    when(mockApi.ssc_info_var_type(p)).thenReturn(1);
    when(mockApi.ssc_info_data_type(p)).thenReturn(1);
    when(mockApi.ssc_info_name(p)).thenReturn("Name");
    when(mockApi.ssc_info_label(p)).thenReturn("Label");
    when(mockApi.ssc_info_units(p)).thenReturn("Units");
    when(mockApi.ssc_info_meta(p)).thenReturn("Meta");
    when(mockApi.ssc_info_group(p)).thenReturn("Group");
    when(mockApi.ssc_info_required(p)).thenReturn("Required");

    List<Variable> vars = m.getVariables();

    Variable expected = Variable.builder().setVariableType(Variable.VariableType.forInt(1))
        .setDataType(Variable.DataType.forInt(1)).setName("Name").setLabel("Label").setUnits("Units")
        .setMeta("Meta").setGroup("Group").setRequired("Required").build();

    assertThat(vars).hasSize(1);
    assertThat(vars.get(0)).isEqualTo(expected);
  }

  private SscModule getRealModule() {
    Pointer fakePointer = mock(Pointer.class);
    when(mockApi.ssc_module_create(anyString())).thenReturn(fakePointer);
    when(mockApi.ssc_module_entry(isA(Integer.class))).thenReturn(fakePointer);
    when(mockApi.ssc_entry_name(isA(Pointer.class))).thenReturn(MODULE_NAME);

    SscModule m = new SscModule(MODULE_NAME, mockApi);
    return m;
  }
}
