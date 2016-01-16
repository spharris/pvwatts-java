package io.github.spharris.ssc;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jnr.ffi.LibraryLoader;
import jnr.ffi.Pointer;
import jnr.ffi.byref.FloatByReference;
import jnr.ffi.byref.IntByReference;

public class IntegrationTestSsc {
	
	private Ssc ssclib;
	private Pointer data;
	
	@Before
	public void loadLibrary() {
		String path = getLibPath();
		ssclib = LibraryLoader.create(Ssc.class)
			.search(path + "/osx64")
			.load("ssc");
	}
	
	private String getLibPath() {
		String basePath = getClass().getClassLoader().getResource("ssc").getPath();
		return basePath;
	}
	
	@Before
	public void prepareData() {
		data = ssclib.ssc_data_create();
	}
	
	@After
	public void cleanupData() {
		ssclib.ssc_data_free(data);
	}
	
	@Test
	public void basicInfoPopulatesCorrectly() {
		assertThat(ssclib.ssc_build_info(), not(equalTo(null)));
		assertThat(ssclib.ssc_version(), equalTo(41));
	}
	
	@Test
	public void dataIsLoaded() {
		assertThat(data, not(equalTo(null)));
	}
	
	@Test
	public void setNumber() {
		float targetVal = 0.5f;
		String name = "test";
		
		ssclib.ssc_data_set_number(data, name, targetVal);
		FloatByReference out = new FloatByReference();
		boolean status = ssclib.ssc_data_get_number(data, name, out);
		
		assertThat(status, equalTo(true));
		assertThat(out.floatValue(), equalTo(targetVal));
	}
	
	@Test
	public void setString() {
		String targetString = "test string";
		String name = "test";
		
		ssclib.ssc_data_set_string(data, name, targetString);
		String result = ssclib.ssc_data_get_string(data, name);
		
		assertThat(result, equalTo(targetString));
	}
	
	@Test
	public void setArray() {
		float value = 0.5f;
		float[] array = { value, value, value, value };
		String name = "test";
		
		ssclib.ssc_data_set_array(data, name, array, array.length);
		IntByReference length = new IntByReference();
		Pointer result = ssclib.ssc_data_get_array(data, name, length);

		assertThat(length.intValue(), equalTo(array.length));
		for (int i = 0; i < length.intValue(); i++) {
			// Offset is by byte (float = 4 bytes)
			assertThat(result.getFloat(i*4), equalTo(value));
		}
	}
	
	@Test
	public void setMatrix() {
		float value = 0.5f;
		int n = 2;
		float[] array = { value, value, value, value };
		String name = "test";
		
		ssclib.ssc_data_set_matrix(data, name, array, n, n);
		IntByReference rows = new IntByReference();
		IntByReference cols = new IntByReference();
		Pointer result = ssclib.ssc_data_get_matrix(data, name, rows, cols);

		assertThat(rows.intValue(), equalTo(n));
		assertThat(cols.intValue(), equalTo(n));
		for (int i = 0; i < rows.intValue(); i++) {
			for (int j = 0; j < cols.intValue(); j++) {
				// Offset is by byte (float = 4 bytes)
				assertThat(result.getFloat((i*j+j)*4), equalTo(value));
			}
		}
	}
	
	@Test
	public void setTable() {
		Pointer table = ssclib.ssc_data_create();
		String name = "test";
		String val = "test string";
		
		ssclib.ssc_data_set_string(table, name, val);
		ssclib.ssc_data_set_table(data, name, table);
		
		Pointer resultTable = ssclib.ssc_data_get_table(data, name);
		String resultString = ssclib.ssc_data_get_string(resultTable, name);
		
		assertThat(resultString, equalTo(val));
	}
	
	@Test
	public void dataClear() {
		String name = "test";
		String targetString = "test string";
		
		ssclib.ssc_data_set_string(data, name, targetString);
		ssclib.ssc_data_clear(data);
		
		String resultString = ssclib.ssc_data_get_string(data, name);
		assertThat(resultString, equalTo(null));
	}
	
	@Test
	public void dataQuery() {
		String name = "test";
		String targetString = "test string";
		
		ssclib.ssc_data_set_string(data, name, targetString);
		int type = ssclib.ssc_data_query(data, name);
		
		assertThat(type, equalTo(1));
	}
	
	@Test
	public void dataFirstWithNoDataReturnsNull() {
		assertThat(ssclib.ssc_data_first(data), equalTo(null));
	}
	
	@Test
	public void dataFirstWithData() {
		String name = "test";
		String targetString = "test string";
		
		ssclib.ssc_data_set_string(data, name, targetString);
		String result = ssclib.ssc_data_first(data);
		
		assertThat(result, equalTo(name));
	}
	
	@Test
	public void entryData() {
		int i = 0;
		Pointer entry = ssclib.ssc_module_entry(i);
		do {
			assertThat(entry, not(equalTo(null)));
			
			String name = ssclib.ssc_entry_name(entry);
			assertThat(name, not(equalTo(null)));
			
			String description = ssclib.ssc_entry_description(entry);
			assertThat(description, not(equalTo(null)));
			
			int version = ssclib.ssc_entry_version(entry);
			assertThat(version, greaterThanOrEqualTo(0));
			
			i++;
			entry = ssclib.ssc_module_entry(i);
		} while (entry != null);
	}
	
	@Test
	public void unknownModuleReturnsNull() {
		Pointer module = ssclib.ssc_module_create("unknown");
		assertThat(module, equalTo(null));
	}
	
	@Test
	public void createAndFreeModule() {
		Pointer module = ssclib.ssc_module_create("pvwattsv1");
		assertThat(module, not(equalTo(null)));
		
		ssclib.ssc_module_free(module);
	}
	
	@Test
	public void getModuleInfo() {
		Pointer module = ssclib.ssc_module_create("pvwattsv1");
		
		int i = 0;
		Pointer info = ssclib.ssc_module_var_info(module, i);
		do {
			assertThat(info, not(equalTo(null)));
			
			ssclib.ssc_info_name(info);
			assertThat(ssclib.ssc_info_var_type(info), greaterThanOrEqualTo(0));
			assertThat(ssclib.ssc_info_data_type(info), greaterThanOrEqualTo(0));
			assertThat(ssclib.ssc_info_name(info), not(equalTo(null)));
			assertThat(ssclib.ssc_info_label(info), not(equalTo(null)));
			assertThat(ssclib.ssc_info_units(info), not(equalTo(null)));
			assertThat(ssclib.ssc_info_meta(info), not(equalTo(null)));
			assertThat(ssclib.ssc_info_group(info), not(equalTo(null)));
			assertThat(ssclib.ssc_info_required(info), not(equalTo(null)));
			assertThat(ssclib.ssc_info_constraints(info), not(equalTo(null)));
			
			i++;
			info = ssclib.ssc_module_var_info(module, i);
		} while (info != null);
		
		ssclib.ssc_module_free(module);
	}
	
	@Test
	public void moduleExecSimple() {
		// Arrange
		initializeSimulationData(ssclib, data);
		
		// Act
		boolean result = ssclib.ssc_module_exec_simple("layoutarea", data);

		// Assert
		assertThat(result, equalTo(true));
		checkSimulationData(ssclib, data);
	}
	
	@Test
	public void moduleExecNoThread() {
		initializeSimulationData(ssclib, data);
		
		String result = ssclib.ssc_module_exec_simple_nothread("layoutarea", data);
		
		assertThat(result, equalTo(null));
		checkSimulationData(ssclib, data);
	}
	
	@Test
	public void moduleExec() {
		initializeSimulationData(ssclib, data);
		
		Pointer module = ssclib.ssc_module_create("layoutarea");
		boolean result = ssclib.ssc_module_exec(module, data);
		
		assertThat(result, equalTo(true));
		checkSimulationData(ssclib, data);
		ssclib.ssc_module_free(module);
	}
	
	@Test
	public void testModuleLog() {
		Pointer module = ssclib.ssc_module_create("layoutarea");
		boolean result = ssclib.ssc_module_exec(module, data);
		
		assertThat(result, equalTo(false));
		
		int i = 0;
		IntByReference itemType = new IntByReference();
		FloatByReference time = new FloatByReference();
		String logMsg = ssclib.ssc_module_log(module, i, itemType, time);
		do {
			assertThat(logMsg, not(equalTo(null)));

			assertThat(itemType.intValue(), greaterThan(0));
			assertThat(time.floatValue(), not(equalTo((0f))));
			
			i++;
			logMsg = ssclib.ssc_module_log(module, i, itemType, time);
		} while (logMsg != null);
	}
	
	private static class TestHandler implements ExecutionHandler {

		@Override
		public boolean update(Pointer module, Pointer handler, int action, float f0, float f1, String s0,
				String s1, Pointer userData) {
			return true;
		}
	}
	
	@Test
	public void testWithHandler() {
		initializeSimulationData(ssclib, data);
		Pointer module = ssclib.ssc_module_create("layoutarea");
		
		ExecutionHandler handler = new TestHandler();
		boolean result = ssclib.ssc_module_exec_with_handler(module, data, handler, null);
		
		assertThat(result, equalTo(true));
		checkSimulationData(ssclib, data);
		ssclib.ssc_module_free(module);
	}
	
	/**
	 * Private helper that initializes some data for a simulation. Assumes
	 * that we want to use the "layoutarea" module, as it's the simplest.
	 */
	private static void initializeSimulationData(Ssc ssclib, Pointer data) {
		float[] positions = { 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f };
		ssclib.ssc_data_set_matrix(data, "positions", positions, 3, 2);
	}
	
	/**
	 * Private helper to make sure that the data created by the
	 * simulation was set as expected. Assumes that we used the "layoutarea"
	 * module
	 */
	private static void checkSimulationData(Ssc ssclib, Pointer data) {
		FloatByReference val = new FloatByReference();
		boolean status = ssclib.ssc_data_get_number(data, "area", val);
		assertThat(status, equalTo(true));
		assertThat(val.floatValue(), greaterThanOrEqualTo(0f));
		
		IntByReference rows = new IntByReference();
		IntByReference cols = new IntByReference();
		Pointer mtx = ssclib.ssc_data_get_matrix(data, "convex_hull", rows, cols);
		assertThat(mtx, not(equalTo(null)));
		assertThat(rows.intValue(), greaterThan(0));
		assertThat(cols.intValue(), greaterThan(0));
	}
}
