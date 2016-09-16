package io.github.spharris.ssc.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;

import io.github.spharris.ssc.SscModule;

/**
 * Various implementations of {@link InputVariable} and {@link OutputVariable} interfaces
 */
public final class SscVariables {
  
  private SscVariables() {}
  
  private static abstract class AbstractVariable<T> implements SscVariable<T> {
    
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
  public static SscVariable<ImmutableList<Float>> arrayOutput(String varName) {
    return new ArraySscVariable(varName);
  }
  
  private static class ArraySscVariable extends AbstractVariable<ImmutableList<Float>> {
    
    ArraySscVariable(String varName) {
      super(varName);
    }
    
    @Override
    public ImmutableList<Float> get(SscModule module) {
      float[] values = module.getArray(varName).get();
      
      ImmutableList.Builder<Float> builder = ImmutableList.builder();
      for (float value : values) {
        builder.add(value);
      }
      
      return builder.build();
    }
  }
  
  public static SscVariable<Float> numberOutput(String varName) {
    return new NumberSscVariable(varName);
  }
  
  private static class NumberSscVariable extends AbstractVariable<Float> {
    
    NumberSscVariable(String varName) {
      super(varName);
    }
    
    @Override
    public Float get(SscModule module) {
      return module.getNumber(varName).get();
    }
  }
  
  public static SscVariable<String> stringOutput(String varName) {
    return new StringSscVariable(varName);
  }
  
  private static class StringSscVariable extends AbstractVariable<String> {
    
    StringSscVariable(String varName) {
      super(varName);
    }
    
    @Override
    public String get(SscModule module) {
      return module.getString(varName).get();
    }
  }
  
  /*
   * Input-related classes
   */
  public static InputVariable<ImmutableList<Float>> arrayInput(
      final String varName) {
    return new ArrayInputVariable(varName);
  }
  
  private static class ArrayInputVariable extends ArraySscVariable
      implements InputVariable<ImmutableList<Float>> {

    ArrayInputVariable(String varName) {
      super(varName);
    }
    
    @Override
    public void set(ImmutableList<Float> value, SscModule module) {
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
  
  private static class NumberInputVariable extends NumberSscVariable
      implements InputVariable<Float> {

    NumberInputVariable(String varName) {
      super(varName);
    }

    @Override
    public void set(Float value, SscModule module) {
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
  
  private static class IntegerInputVariable extends AbstractVariable<Integer>
      implements InputVariable<Integer> {

    IntegerInputVariable(String varName) {
      super(varName);
    }

    @Override
    public void set(Integer value, SscModule module) {
      if (value == null) {
        return;
      }
      
      checkNotNull(module);
       
      module.setValue(varName, value.floatValue());
    }
    
    @Override
    public Integer get(SscModule module) {
      checkNotNull(module);
      return (int) Math.floor(module.getNumber(varName).get());
    }
  }
  
  public static InputVariable<String> stringInput(String varName) {
    return new StringInputVariable(varName);
  }
  
  private static class StringInputVariable extends StringSscVariable
      implements InputVariable<String> {
    
    StringInputVariable(String varName) {
      super(varName);
    }
    
    @Override
    public void set(String value, SscModule module) {
      if (value == null) {
        return;
      }
      
      checkNotNull(module);
      module.setValue(varName, value);
    }
  }
}
