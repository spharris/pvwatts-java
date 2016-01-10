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

public class TestSsc {
	
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
		int status = ssclib.ssc_data_get_number(data, name, out);
		
		assertThat(status, greaterThan(0));
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
}
