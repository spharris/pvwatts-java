package io.github.spharris.ssc;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class UnitTestVariable {

  @Test
  public void varType() {
    assertThat(Variable.VariableType.forInt(1), equalTo(Variable.VariableType.INPUT));
    assertThat(Variable.VariableType.forInt(2), equalTo(Variable.VariableType.OUTPUT));
    assertThat(Variable.VariableType.forInt(3), equalTo(Variable.VariableType.INOUT));
  }

  @Test
  public void dataType() {
    assertThat(Variable.DataType.forInt(0), equalTo(Variable.DataType.INVALID));
    assertThat(Variable.DataType.forInt(1), equalTo(Variable.DataType.STRING));
    assertThat(Variable.DataType.forInt(2), equalTo(Variable.DataType.NUMBER));
    assertThat(Variable.DataType.forInt(3), equalTo(Variable.DataType.ARRAY));
    assertThat(Variable.DataType.forInt(4), equalTo(Variable.DataType.MATRIX));
    assertThat(Variable.DataType.forInt(5), equalTo(Variable.DataType.TABLE));
  }

  @Test
  public void equalsWorks() {
    Variable v1 = Variable.buildVariable().varType(Variable.VariableType.forInt(1))
        .dataType(Variable.DataType.forInt(1)).name("Name").label("Label").units("Units")
        .meta("Meta").group("Group").required("Required").build();

    Variable v2 = Variable.buildVariable().varType(Variable.VariableType.forInt(1))
        .dataType(Variable.DataType.forInt(1)).name("Name").label("Label").units("Units")
        .meta("Meta").group("Group").required("Required").build();

    assertThat(v1, equalTo(v2));
  }
}
