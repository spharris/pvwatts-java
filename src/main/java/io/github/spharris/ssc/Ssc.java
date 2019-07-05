package io.github.spharris.ssc;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;

/*
 * import jnr.ffi.Pointer; import jnr.ffi.annotations.In; import jnr.ffi.annotations.Out; import
 * jnr.ffi.byref.FloatByReference; import jnr.ffi.byref.IntByReference;
 */

/**
 * Raw interface to the Ssc library. Declares the basic functions defined in sscapi.h. This
 * interface is public for compatibility with jnr.ffi, but should not be used outside of the ssc
 * package. Instead, use a {@link io.github.spharris.ssc.SscModule}
 *
 * @author spharris
 */
public interface Ssc extends Library {

  static final String SSC_LIB_NAME = "ssc";

  int ssc_version();

  String ssc_build_info();

  /*
   * Data object management functions
   */
  Pointer ssc_data_create();

  void ssc_data_free(Pointer data);

  void ssc_data_clear(Pointer data);

  void ssc_data_unassign(Pointer data, String name);

  int ssc_data_query(Pointer data, String name);

  String ssc_data_first(Pointer data);

  String ssc_data_next(Pointer data);

  /*
   * Setters and getters for different data types
   */
  void ssc_data_set_number(Pointer data, String name, float value);

  void ssc_data_set_string(Pointer data, String name, String value);

  void ssc_data_set_array(Pointer data, String name, float[] values, int length);

  void ssc_data_set_matrix(Pointer data, String name, float[] values, int rows, int cols);

  void ssc_data_set_table(Pointer data, String name, Pointer table);

  boolean ssc_data_get_number(Pointer data, String name, FloatByReference value);

  String ssc_data_get_string(Pointer data, String name);

  Pointer ssc_data_get_array(Pointer data, String name, IntByReference lenth);

  Pointer ssc_data_get_matrix(Pointer data, String name, IntByReference rows, IntByReference cols);

  Pointer ssc_data_get_table(Pointer data, String name);

  /*
   * Module metadata functions
   */
  Pointer ssc_module_entry(int index);

  String ssc_entry_name(Pointer entry);

  String ssc_entry_description(Pointer entry);

  int ssc_entry_version(Pointer entry);

  /*
   * Module creation functions
   */
  Pointer ssc_module_create(String moduleName);

  void ssc_module_free(Pointer module);

  /*
   * Info functions
   */
  Pointer ssc_module_var_info(Pointer module, int index);

  int ssc_info_var_type(Pointer info);

  int ssc_info_data_type(Pointer info);

  String ssc_info_name(Pointer info);

  String ssc_info_label(Pointer info);

  String ssc_info_units(Pointer info);

  String ssc_info_meta(Pointer info);

  String ssc_info_group(Pointer info);

  String ssc_info_required(Pointer info);

  String ssc_info_constraints(Pointer info);

  /*
   * Module execution functions
   */
  void ssc_module_exec_set_print(int print);

  boolean ssc_module_exec_simple(String name, Pointer data);

  String ssc_module_exec_simple_nothread(String name, Pointer data);

  boolean ssc_module_exec(Pointer module, Pointer data);

  String ssc_module_log(Pointer module, int index, IntByReference itemType, FloatByReference time);

  /*
   * Module execution with a handler. The user must create a handler to pass in during execution.
   */
  boolean ssc_module_exec_with_handler(
      Pointer module, Pointer data, SscExecutionHandler handler, Pointer userData);
}
