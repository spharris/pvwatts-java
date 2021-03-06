package io.github.spharris.ssc;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class UnitTestVariable {

  @Test
  public void varType() {
    assertThat(Variable.VariableType.forInt(1)).isEqualTo(Variable.VariableType.INPUT);
    assertThat(Variable.VariableType.forInt(2)).isEqualTo(Variable.VariableType.OUTPUT);
    assertThat(Variable.VariableType.forInt(3)).isEqualTo(Variable.VariableType.INOUT);
  }

  @Test
  public void dataType() {
    assertThat(Variable.DataType.forInt(0)).isEqualTo(Variable.DataType.INVALID);
    assertThat(Variable.DataType.forInt(1)).isEqualTo(Variable.DataType.STRING);
    assertThat(Variable.DataType.forInt(2)).isEqualTo(Variable.DataType.NUMBER);
    assertThat(Variable.DataType.forInt(3)).isEqualTo(Variable.DataType.ARRAY);
    assertThat(Variable.DataType.forInt(4)).isEqualTo(Variable.DataType.MATRIX);
    assertThat(Variable.DataType.forInt(5)).isEqualTo(Variable.DataType.TABLE);
  }

  @Test
  public void equalsWorks() {
    Variable v1 =
        Variable.builder()
            .setVariableType(Variable.VariableType.forInt(1))
            .setDataType(Variable.DataType.forInt(1))
            .setName("Name")
            .setLabel("Label")
            .setUnits("Units")
            .setMeta("Meta")
            .setGroup("Group")
            .setRequired("Required")
            .build();

    Variable v2 =
        Variable.builder()
            .setVariableType(Variable.VariableType.forInt(1))
            .setDataType(Variable.DataType.forInt(1))
            .setName("Name")
            .setLabel("Label")
            .setUnits("Units")
            .setMeta("Meta")
            .setGroup("Group")
            .setRequired("Required")
            .build();

    assertThat(v1).isEqualTo(v2);
  }
}
