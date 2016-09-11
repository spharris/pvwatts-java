package io.github.spharris.ssc.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;

import io.github.spharris.ssc.Module;

/**
 * Various implementations of {@link InputVariable} and {@link OutputVariable} interfaces
 */
public final class SscVariables {
  
  private SscVariables() {}
  
  private static class AbstractVariable implements SscVariable {
    
    protected final String varName;
    
    AbstractVariable(String varName) {
      checkNotNull(varName);
      this.varName = varName;
    }
    
    @Override
    public String getName() {
      return varName;
    }
  }
  
  /*
   * Output-related classes
   */
  public static OutputVariable<ImmutableList<Float>> arrayOutput(String varName) {
    return new ArrayOutputVariable(varName);
  }
  
  private static class ArrayOutputVariable extends AbstractVariable 
      implements OutputVariable<ImmutableList<Float>> {
    
    ArrayOutputVariable(String varName) {
      super(varName);
    }
    
    @Override
    public ImmutableList<Float> get(Module module) {
      float[] values = module.getArray(varName).get();
      
      ImmutableList.Builder<Float> builder = ImmutableList.builder();
      for (float value : values) {
        builder.add(value);
      }
      
      return builder.build();
    }
  }
  
  public static OutputVariable<Float> numberOutput(String varName) {
    return new NumberOutputVariable(varName);
  }
  
  private static class NumberOutputVariable extends AbstractVariable
      implements OutputVariable<Float> {
    
    NumberOutputVariable(String varName) {
      super(varName);
    }
    
    @Override
    public Float get(Module module) {
      return module.getNumber(varName).get();
    }
  }
  
  public static OutputVariable<String> stringOutput(String varName) {
    return new StringOutputVariable(varName);
  }
  
  private static class StringOutputVariable extends AbstractVariable
      implements OutputVariable<String> {
    
    StringOutputVariable(String varName) {
      super(varName);
    }
    
    @Override
    public String get(Module module) {
      return module.getString(varName).get();
    }
  }
  
  /*
   * Input-related classes
   */
  public static <T extends Number> InputVariable<ImmutableList<T>> arrayInput(
      final String varName) {
    return new ArrayInputVariable<T>(varName);
  }
  
  private static class ArrayInputVariable<T extends Number> extends AbstractVariable
      implements InputVariable<ImmutableList<T>> {

    ArrayInputVariable(String varName) {
      super(varName);
    }
    
    @Override
    public void set(ImmutableList<T> value, Module module) {
      if (value == null) {
        return;
      }
      
      checkNotNull(module);

      float[] values = new float[value.size()];
      for (int i = 0; i < value.size(); i++) {
        values[i] = value.get(i).floatValue();
      }

      module.setValue(varName, values);
    }
  }
  
  public static InputVariable<Float> numberInput(final String varName) {
    return new NumberInputVariable(varName);
  }
  
  private static class NumberInputVariable extends AbstractVariable
      implements InputVariable<Float> {

    NumberInputVariable(String varName) {
      super(varName);
    }

    @Override
    public void set(Float value, Module module) {
      if (value == null) {
        return;
      }
      
      checkNotNull(module);
       
      module.setValue(varName, value.floatValue());
    }
  }
  
  public static InputVariable<Integer> integerInput(final String varName) {
    return new IntegerInputVariable(varName);
  }
  
  private static class IntegerInputVariable extends AbstractVariable
      implements InputVariable<Integer> {

    IntegerInputVariable(String varName) {
      super(varName);
    }

    @Override
    public void set(Integer value, Module module) {
      if (value == null) {
        return;
      }
      
      checkNotNull(module);
       
      module.setValue(varName, value.floatValue());
    }
  }
  
  public static InputVariable<String> stringInput(String varName) {
    return new StringInputVariable(varName);
  }
  
  private static class StringInputVariable extends AbstractVariable
      implements InputVariable<String> {
    
    StringInputVariable(String varName) {
      super(varName);
    }
    
    @Override
    public void set(String value, Module module) {
      if (value == null) {
        return;
      }
      
      checkNotNull(module);
      module.setValue(varName, value);
    }
  }
}
