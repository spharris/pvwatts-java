package io.github.spharris.ssc;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;

@RunWith(MockitoJUnitRunner.class)
public class UnitTestDataContainer {

  @Mock Ssc mockApi;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  DataContainer data;
  
  @Before
  public void createData() {
    when(mockApi.ssc_data_create()).thenReturn(mock(Pointer.class));
    data = new DataContainer(mockApi);
  }
  
  @Test
  public void setNumber() {
    String varName = "var";
    float value = 0.5f;
    data.setNumber(varName, value);

    verify(mockApi).ssc_data_set_number(any(Pointer.class), eq(varName), eq(value));
  }

  @Test
  public void getNonExistentNumber() {
    when(mockApi.ssc_data_get_number(any(Pointer.class), any(String.class),
        any(FloatByReference.class))).thenReturn(false);

    Optional<Float> val = data.getNumber("asdf");
    assertThat(val.isPresent()).isFalse();
  }

  @Test
  public void setString() {
    String varName = "var";
    float value = 0.5f;
    data.setNumber(varName, value);

    verify(mockApi).ssc_data_set_number(any(Pointer.class), eq(varName), eq(value));
  }

  @Test
  public void getNonExistentString() {
    when(mockApi.ssc_data_get_string(any(Pointer.class), any(String.class))).thenReturn(null);

    Optional<String> val = data.getString("asdf");
    assertThat(val.isPresent()).isFalse();
  }

  @Test
  public void getString() {
    String returnVal = "Hello, world!";
    when(mockApi.ssc_data_get_string(any(Pointer.class), any(String.class))).thenReturn(returnVal);

    Optional<String> val = data.getString("asdf");
    assertThat(val.isPresent()).isTrue();
    assertThat(val.get()).isEqualTo(returnVal);
  }

  @Test
  public void setNumberThrowsErrorOnFreedModule() {
    data.free();

    thrown.expect(IllegalStateException.class);

    data.setNumber("var", 0f);
  }

  @Test
  public void freeCallsSccFree() {
    data.free();

    verify(mockApi).ssc_data_free(any(Pointer.class));
  }

  @Test
  public void multipleFreeCallsOnlyFreesOnce() {
    data.free();
    data.free();

    verify(mockApi, times(1)).ssc_data_free(any(Pointer.class));
  }

  @Test
  public void arrayLengthGreaterThanZero() {
    float[] input = new float[0];
    thrown.expect(IllegalArgumentException.class);

    data.setArray("asdf", input);
  }

  @Test
  public void mtxRowsGreaterThanZero() {
    float[][] input = new float[0][1];
    thrown.expect(IllegalArgumentException.class);

    data.setMatrix("asdf", input);
  }

  @Test
  public void mtxColsGreaterThanZero() {
    float[][] input = new float[1][0];
    thrown.expect(IllegalArgumentException.class);

    data.setMatrix("asdf", input);
  }

  @Test
  public void callsSetMatrixProperly() {
    float[][] value = {{1, 2}, {3, 4}};

    String varName = "adsf";
    data.setMatrix(varName, value);

    float[] expected = {1, 2, 3, 4};
    verify(mockApi).ssc_data_set_matrix(any(Pointer.class), eq(varName), eq(expected), eq(2),
        eq(2));
  }

  @Test
  public void callsSetMatrixProperlyMoreRows() {
    float[][] value = {{1, 2}, {3, 4}, {5, 6}};

    String varName = "adsf";
    data.setMatrix(varName, value);

    float[] expected = {1, 2, 3, 4, 5, 6};
    verify(mockApi).ssc_data_set_matrix(any(Pointer.class), eq(varName), eq(expected), eq(3),
        eq(2));
  }
}
