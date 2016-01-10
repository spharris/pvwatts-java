package io.github.spharris.ssc;

import jnr.ffi.Pointer;
import jnr.ffi.annotations.Out;
import jnr.ffi.byref.FloatByReference;
import jnr.ffi.byref.IntByReference;

/**
 * Raw interface to the Ssc library. Declares the basic functions defined in
 * sscapi.h
 * 
 * @author spharris
 */
public interface Ssc {
	public int ssc_version();
	public String ssc_build_info();
	
	public Pointer ssc_data_create();
	public void ssc_data_free(Pointer data);
	
	public void ssc_data_set_number(Pointer data, String name, float value);
	public void ssc_data_set_string(Pointer data, String name, String value);
	public void ssc_data_set_array(Pointer data, String name, float[] values, int length);
	public void ssc_data_set_matrix(Pointer data, String name, float[] values, int rows, int cols);
	public void ssc_data_set_table(Pointer data, String name, Pointer table);
	
	public int ssc_data_get_number(Pointer data, String name, @Out FloatByReference value);
	public String ssc_data_get_string(Pointer data, String name);
	public Pointer ssc_data_get_array(Pointer data, String name, @Out IntByReference lenth);
	public Pointer ssc_data_get_matrix(Pointer data, String name, @Out IntByReference rows, @Out IntByReference cols);
	public Pointer ssc_data_get_table(Pointer data, String name);
}
