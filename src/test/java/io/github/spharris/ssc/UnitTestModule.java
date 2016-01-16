package io.github.spharris.ssc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.base.Optional;

import io.github.spharris.ssc.excepions.UnknownModuleNameException;
import jnr.ffi.Pointer;
import jnr.ffi.byref.FloatByReference;

public class UnitTestModule {
	
	Ssc mockApi;
	
	@Before
	public void createMockApi() {
		mockApi = mock(Ssc.class);
	}
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void throwsExceptiononUnknownModule() {
		when(mockApi.ssc_module_create(anyString())).thenReturn(null);
		
		thrown.expect(UnknownModuleNameException.class);
		
		new Module("asdkfsd", mockApi);
	}
	
	@Test
	public void setNumber() {
		returnRealModule();
		Module m = new Module("adf", mockApi);
		
		String varName = "var";
		float value = 0.5f;
		m.setNumber(varName, value);
		
		verify(mockApi).ssc_data_set_number(any(Pointer.class), eq(varName), eq(value));
	}
	
	@Test
	public void getNonExistentNumber() {
		Module m = returnRealModule();
		when(mockApi.ssc_data_get_number(any(Pointer.class), any(String.class), any(FloatByReference.class)))
			.thenReturn(false);
		
		Optional<Float> val = m.getNumber("asdf");
		assertThat(val.isPresent(), equalTo(false));
	}
	
	@Test
	public void setString() {
		returnRealModule();
		Module m = new Module("adf", mockApi);
		
		String varName = "var";
		float value = 0.5f;
		m.setNumber(varName, value);
		
		verify(mockApi).ssc_data_set_number(any(Pointer.class), eq(varName), eq(value));
	}
	
	@Test
	public void getNonExistentString() {
		Module m = returnRealModule();
		when(mockApi.ssc_data_get_string(any(Pointer.class), any(String.class)))
			.thenReturn(null);
		
		Optional<String> val = m.getString("asdf");
		assertThat(val.isPresent(), equalTo(false));
	}
	
	@Test
	public void getString() {
		Module m = returnRealModule();
		String returnVal = "Hello, world!";
		when(mockApi.ssc_data_get_string(any(Pointer.class), any(String.class)))
			.thenReturn(returnVal);
		
		Optional<String> val = m.getString("asdf");
		assertThat(val.isPresent(), equalTo(true));
		assertThat(val.get(), equalTo(returnVal));
	}
	
	@Test
	public void setNumberThrowsErrorOnFreedModule() {
		Module m = returnFreedModule();
		
		thrown.expect(IllegalStateException.class);
		
		m.setNumber("var", 0f);
	}
	
	@Test
	public void freeCallsSccFree() {
		Module m = returnRealModule();
		
		m.free();
		
		verify(mockApi).ssc_module_free(any(Pointer.class));
	}
	
	@Test
	public void multipleFreeCallsOnlyFreesOnce() {
		Module m = returnRealModule();
		
		m.free();
		m.free();
		
		verify(mockApi, times(1)).ssc_module_free(any(Pointer.class));
	}
	
	private Module returnRealModule() {
		Pointer fakePointer = mock(Pointer.class);
		when(mockApi.ssc_module_create(anyString())).thenReturn(fakePointer);
		
		Module m = new Module("adf", mockApi);
		return m;
	}
	
	private Module returnFreedModule() {
		Module m = returnRealModule();
		m.free();
		
		return m;
	}
}
