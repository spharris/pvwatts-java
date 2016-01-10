package io.github.spharris.ssc;

import jnr.ffi.Pointer;
import jnr.ffi.annotations.In;
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
	
	/*
	 * Data object management functions
	 */
	public Pointer ssc_data_create();
	public void ssc_data_free(Pointer data);
	public void ssc_data_clear(Pointer data);
	public void ssc_data_unassign(Pointer data, @In String name);
	public int ssc_data_query(Pointer data, @In String name);
	public String ssc_data_first(Pointer data);
	public String ssc_data_next(Pointer data);
	
	/*
	 * Setters and getters for different data types
	 */
	public void ssc_data_set_number(Pointer data, @In String name, float value);
	public void ssc_data_set_string(Pointer data, @In String name, @In String value);
	public void ssc_data_set_array(Pointer data, @In String name, @In float[] values, int length);
	public void ssc_data_set_matrix(Pointer data, @In String name, @In float[] values, int rows, int cols);
	public void ssc_data_set_table(Pointer data, @In String name, Pointer table);
	
	public boolean ssc_data_get_number(Pointer data, @In String name, @Out FloatByReference value);
	public String ssc_data_get_string(Pointer data, @In String name);
	public Pointer ssc_data_get_array(Pointer data, @In String name, @Out IntByReference lenth);
	public Pointer ssc_data_get_matrix(Pointer data, @In String name, @Out IntByReference rows, @Out IntByReference cols);
	public Pointer ssc_data_get_table(Pointer data, @In String name);
	
	/*
	 * Module metadata functions
	 */
	public Pointer ssc_module_entry(int index);
	public String ssc_entry_name(Pointer entry);
	public String ssc_entry_description(Pointer entry);
	public int ssc_entry_version(Pointer entry);
	
	/*
	 * Module creation functions
	 */
	public Pointer ssc_module_create(@In String moduleName);
	public void ssc_module_free(Pointer module);

	/*
	 * Info functions
	 */
	public Pointer ssc_module_var_info(Pointer module, int index);
	public int ssc_info_var_type(Pointer info);
	public int ssc_info_data_type(Pointer info);
	public String ssc_info_name(Pointer info);
	public String ssc_info_label(Pointer info);
	public String ssc_info_units(Pointer info);
	public String ssc_info_meta(Pointer info);
	public String ssc_info_group(Pointer info);
	public String ssc_info_required(Pointer info);
	public String ssc_info_constraints(Pointer info);
	
	/*
	 * Module execution functions
	 */
	public void ssc_module_exec_set_print(int print);
	public boolean ssc_module_exec_simple(@In String name, Pointer data);
	public String ssc_module_exec_simple_nothread(@In String name, Pointer data);
	public boolean ssc_module_exec(Pointer module, Pointer data);
	public String ssc_module_log(Pointer module, int index, @Out IntByReference itemType, @Out FloatByReference time);
	
	/*
	 * Module execution with a handler. The user must create a handler to pass in during
	 * execution.
	 */
	public boolean ssc_module_exec_with_handler(Pointer module, Pointer data,
			ExecutionHandler handler, Pointer userData);
}
