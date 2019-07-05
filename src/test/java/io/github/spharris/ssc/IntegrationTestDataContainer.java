package io.github.spharris.ssc;

import static com.google.common.truth.Truth.assertThat;

import com.google.inject.Guice;
import java.util.Optional;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class IntegrationTestDataContainer {

  static final float EPSILON = 0f;

  @Inject DataContainer data;

  @Before
  public void createInjector() {
    Guice.createInjector(new SscGuiceModule()).injectMembers(this);
  }

  @After
  public void freeData() {
    data.free();
  }

  @Test
  public void getNumber() {
    String field = "field";
    float value = 0.5f;

    data.setNumber(field, value);
    Optional<Float> result = data.getNumber(field);

    assertThat(result.isPresent()).isTrue();
    assertThat(result.get()).isWithin(EPSILON).of(value);
  }

  @Test
  public void getArray() {
    String field = "field";
    float[] values = {0.5f, 0.5f, 0.5f};

    data.setArray(field, values);
    Optional<float[]> result = data.getArray(field);

    assertThat(result.isPresent()).isTrue();
    assertThat(result.get()).hasValuesWithin(EPSILON).of(values);
  }

  @Test
  public void callsGetMatrixProperly() {
    String field = "field";
    float[][] values = {{1, 2}, {3, 4}};

    data.setMatrix(field, values);
    Optional<float[][]> result = data.getMatrix(field);

    assertThat(result.isPresent()).isTrue();
    assertThat(result.get()).isEqualTo(values);
  }

  @Test
  public void callsGetMatrixProperlyMoreRows() {
    String field = "field";
    float[][] values = {{1, 2}, {3, 4}, {5, 6}};

    data.setMatrix(field, values);
    Optional<float[][]> result = data.getMatrix(field);

    assertThat(result.isPresent()).isTrue();
    assertThat(result.get()).isEqualTo(values);
  }

  @Test
  public void callsGetMatrixProperlyMoreCols() {
    String field = "field";
    float[][] values = {{1, 2, 3}, {4, 5, 6}};

    data.setMatrix(field, values);
    Optional<float[][]> result = data.getMatrix(field);

    assertThat(result.isPresent()).isTrue();
    assertThat(result.get()).isEqualTo(values);
  }
}
