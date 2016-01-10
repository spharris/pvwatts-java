package io.github.spharris.ssc;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jnr.ffi.LibraryLoader;
import jnr.ffi.Pointer;
import jnr.ffi.byref.FloatByReference;

public class TestSsc {
	
	private Ssc ssclib;
	private Pointer data;
	
	@Before
	public void loadLibrary() {
		ssclib = LibraryLoader.create(Ssc.class)
			.search("/Users/spharris/Dropbox/pvwatts-java/target/classes/ssc/osx64")
			.load("ssc");
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
		assertThat(out.getValue().floatValue(), equalTo(targetVal));
	}
	
	@Test
	public void setString() {
		String targetString = "test string";
		String name = "test";
		
		ssclib.ssc_data_set_string(data, name, targetString);
		String result = ssclib.ssc_data_get_string(data, name);
		
		assertThat(result, equalTo(targetString));
	}
}
