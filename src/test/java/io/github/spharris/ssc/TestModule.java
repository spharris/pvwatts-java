package io.github.spharris.ssc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.github.spharris.ssc.excepions.UnknownModuleNameException;
import jnr.ffi.Pointer;

public class TestModule {
	
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
	
	private void returnRealModule() {
		Pointer fakePointer = mock(Pointer.class);
		when(mockApi.ssc_module_create(anyString())).thenReturn(fakePointer);
	}
}
