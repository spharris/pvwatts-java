package io.github.spharris.ssc;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * An immutable class representing a module variable.
 */
@JsonPropertyOrder({"dataType", "variableType", "name", "label", "units", "group", "required",
    "meta"})
public class Variable {

  private final VariableType varType;
  private final DataType dataType;
  private final String name;
  private final String label;
  private final String units;
  private final String meta;
  private final String group;
  private final String required;

  /**
   * Since this is a value object that can only come from a module, don't let people construct it on
   * their own.
   */
  private Variable(VariableType varType, DataType dataType, String name, String label, String units,
      String meta, String group, String required) {
    this.varType = varType;
    this.dataType = dataType;
    this.name = name;
    this.label = label;
    this.units = units;
    this.meta = meta;
    this.group = group;
    this.required = required;
  }

  static Builder buildVariable() {
    return new Builder();
  }

  static class Builder {
    private VariableType varType;
    private DataType dataType;
    private String name;
    private String label;
    private String units;
    private String meta;
    private String group;
    private String required;

    Builder varType(VariableType value) {
      varType = value;
      return this;
    }

    Builder dataType(DataType value) {
      dataType = value;
      return this;
    }

    Builder name(String value) {
      name = value;
      return this;
    }

    Builder label(String value) {
      label = value;
      return this;
    }

    Builder units(String value) {
      units = value;
      return this;
    }

    Builder meta(String value) {
      meta = value;
      return this;
    }

    Builder group(String value) {
      group = value;
      return this;
    }

    Builder required(String value) {
      required = value;
      return this;
    }

    Variable build() {
      return new Variable(varType, dataType, name, label, units, meta, group, required);
    }
  }

  public enum VariableType {
    INPUT(1), OUTPUT(2), INOUT(3);

    private int sscConst;

    private static Map<Integer, Variable.VariableType> sscVarTypeMap = new HashMap<>();
    static {
      for (Variable.VariableType v : values()) {
        sscVarTypeMap.put(v.sscConst, v);
      }
    }

    private VariableType(final int sscConst) {
      this.sscConst = sscConst;
    }

    public static Variable.VariableType forInt(int val) {
      return sscVarTypeMap.get(val);
    }
  }

  public enum DataType {
    INVALID(0, null), STRING(1, String.class), NUMBER(2, Float.class), ARRAY(3,
        float[].class), MATRIX(4, float[][].class), TABLE(5, null);

    private int sscConst;
    private Class<?> clazz;

    private static Map<Integer, Variable.DataType> sscDataTypeMap = new HashMap<>();
    static {
      for (Variable.DataType v : values()) {
        sscDataTypeMap.put(v.sscConst, v);
      }
    }

    private DataType(final int sscConst, Class<?> clazz) {
      this.sscConst = sscConst;
      this.clazz = clazz;
    }

    public static Variable.DataType forInt(int val) {
      return sscDataTypeMap.get(val);
    }

    public Class<?> javaType() {
      return clazz;
    }
  }

  public VariableType getVariableType() {
    return varType;
  }

  public DataType getDataType() {
    return dataType;
  }

  public String getName() {
    return name;
  }

  public String getLabel() {
    return label;
  }

  public String getUnits() {
    return units;
  }

  public String getMeta() {
    return meta;
  }

  public String getGroup() {
    return group;
  }

  public String getRequired() {
    return required;
  }

  @Override
  public String toString() {
    return String.format("name: %s, type: %s, data: %s", name, varType, dataType);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Variable)) {
      return false;
    }

    Variable otherVar = (Variable) o;
    return otherVar.getVariableType() == varType && otherVar.getDataType() == dataType
        && Objects.equals(otherVar.getName(), name) && Objects.equals(otherVar.getLabel(), label)
        && Objects.equals(otherVar.getUnits(), units) && Objects.equals(otherVar.getMeta(), meta)
        && Objects.equals(otherVar.getGroup(), group)
        && Objects.equals(otherVar.getRequired(), required);
  }

  @Override
  public int hashCode() {
    return Objects.hash(varType, dataType, name, label, units, meta, group, required);
  }
}
