package io.github.spharris.ssc.utils;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import io.github.spharris.ssc.Module;

@RunWith(MockitoJUnitRunner.class)
public class UnitTestSscVariables {
  
  private static final String VAR_NAME = "test_var_name";

  @Rule public ExpectedException thrown = ExpectedException.none();
  
  @Mock private Module module;
  
  @Before
  public void mockModule() {
    module = mock(Module.class);
  }
  
  @Test
  public void numberInputCallsCorrectFunction() {
    InputVariable<Float> input = SscVariables.numberInput(VAR_NAME);
    float value = 1.0f;
    
    input.set(value, module);
    
    verify(module).setValue(VAR_NAME, value);
  }
  
  @Test
  public void arrayInputCallsCorrectFunction() {
    InputVariable<ImmutableList<Float>> input = SscVariables.arrayInput(VAR_NAME);
    float[] expected = new float[] { 1.0f };
    
    input.set(ImmutableList.of(1.0f), module);
    
    verify(module).setValue(VAR_NAME, expected);
  }
  
  @Test
  public void stringInputCallsCorrectFunction() {
    InputVariable<String> input = SscVariables.stringInput(VAR_NAME);
    String value = "value";
    
    input.set(value, module);
    
    verify(module).setValue(VAR_NAME, value);
  }
  
  @Test
  public void numberOutputCallsCorrectFunction() {
    when(module.getNumber(isA(String.class))).thenReturn(Optional.of(1.0f));
    SscVariable<Float> output = SscVariables.numberOutput(VAR_NAME);
    
    output.get(module);
    
    verify(module).getNumber(VAR_NAME);
  }
  
  @Test
  public void arrayOutputCallsCorrectFunction() {
    when(module.getArray(isA(String.class))).thenReturn(Optional.of(new float[] {1.0f}));
    SscVariable<ImmutableList<Float>> output = SscVariables.arrayOutput(VAR_NAME);
    
    output.get(module);
    
    verify(module).getArray(VAR_NAME);
  }
  
  @Test
  public void stringOutputCallsCorrectFunction() {
    when(module.getString(isA(String.class))).thenReturn(Optional.of("test string"));
    SscVariable<String> output = SscVariables.stringOutput(VAR_NAME);
    
    output.get(module);
    
    verify(module).getString(VAR_NAME);
  }
}
