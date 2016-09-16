package io.github.spharris.ssc;

import com.google.auto.value.AutoValue;

/**
 * An immutable class representing a module variable.
 */
@AutoValue
public abstract class Variable {
  
  Variable() {}
  
  public abstract VariableType getVariableType();
  public abstract DataType getDataType();
  public abstract String getName();
  public abstract String getLabel();
  public abstract String getUnits();
  public abstract String getMeta();
  public abstract String getGroup();
  public abstract String getRequired();

  static Builder builder() {
    return new AutoValue_Variable.Builder();
  }

  @AutoValue.Builder
  static abstract class Builder {

    abstract Builder setVariableType(VariableType value);
    abstract Builder setDataType(DataType value);
    abstract Builder setName(String value);
    abstract Builder setLabel(String value);
    abstract Builder setUnits(String value);
    abstract Builder setMeta(String value);
    abstract Builder setGroup(String value);
    abstract Builder setRequired(String value);
    
    abstract Variable build();
  }

  public enum VariableType {
    INPUT(1), OUTPUT(2), INOUT(3);

    private final int sscConst;

    private VariableType(int sscConst) {
      this.sscConst = sscConst;
    }

    public static Variable.VariableType forInt(int val) {
      for (VariableType var : values()) {
        if (var.sscConst == val) {
          return var;
        }
      }
      
      return null;
    }
  }

  public enum DataType {
    INVALID(0),
    STRING(1),
    NUMBER(2),
    ARRAY(3),
    MATRIX(4),
    TABLE(5);

    private final int sscConst;

    private DataType(int sscConst) {
      this.sscConst = sscConst;
    }

    public static DataType forInt(int val) {
      for (DataType var : values()) {
        if (var.sscConst == val) {
          return var;
        }
      }
      
      return null;
    }
  }
}
