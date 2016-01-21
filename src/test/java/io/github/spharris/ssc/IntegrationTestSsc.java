package io.github.spharris.ssc;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;

public class IntegrationTestSsc {
	
	private Ssc ssclib;
	private Pointer data;
	
	@Before
	public void loadLibrary() {
		ssclib = (Ssc)Native.loadLibrary("ssc", Ssc.class);
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
		assertThat(out.getValue(), equalTo(targetVal));
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

		assertThat(length.getValue(), equalTo(array.length));
		for (int i = 0; i < length.getValue(); i++) {
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

		assertThat(rows.getValue(), equalTo(n));
		assertThat(cols.getValue(), equalTo(n));
		for (int i = 0; i < rows.getValue(); i++) {
			for (int j = 0; j < cols.getValue(); j++) {
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

			assertThat(itemType.getValue(), greaterThan(0));
			assertThat(time.getValue(), not(equalTo((0f))));
			
			i++;
			logMsg = ssclib.ssc_module_log(module, i, itemType, time);
		} while (logMsg != null);
	}
	
	public static class TestHandler implements SscExecutionHandler {

		@Override
		public boolean update(Pointer module, Pointer handler, int action, float f0, float f1, String s0,
				String s1, Pointer userData) {
			
			assertThat(f0, greaterThanOrEqualTo(0f));
			assertThat(f1, greaterThanOrEqualTo(0f));

			return true;
		}
	}
	
	@Test
	public void testWithHandler() {
		initializeComplicatedData(ssclib, data);
		Pointer module = ssclib.ssc_module_create("pvsamv1");
		
		SscExecutionHandler handler = new TestHandler();
		boolean result = ssclib.ssc_module_exec_with_handler(module, data, handler, null);
		
		assertThat(result, equalTo(true));	
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
	
	private static void initializeComplicatedData(Ssc ssclib, Pointer data) {
		String weatherFile = IntegrationTestModule.class.getClassLoader().getResource("weather/23129.tm2").getPath();
		ssclib.ssc_data_set_string(data, "solar_resource_file", weatherFile);

		float[] albedo= new float[] {0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f};
		ssclib.ssc_data_set_array(data, "albedo", albedo, 12);

		ssclib.ssc_data_set_number(data, "system_capacity", 0.24f);
		ssclib.ssc_data_set_number(data, "modules_per_string", 1);
		ssclib.ssc_data_set_number(data, "strings_in_parallel", 1);
		ssclib.ssc_data_set_number(data, "inverter_count", 1);

		ssclib.ssc_data_set_number(data, "ac_loss", 0.1f);
		ssclib.ssc_data_set_number(data, "acwiring_loss", 0.015f);
		ssclib.ssc_data_set_number(data, "transformer_loss", 0.02f);

		ssclib.ssc_data_set_number(data, "subarray1_tilt", 20);
		ssclib.ssc_data_set_number(data, "subarray1_track_mode", 0);
		ssclib.ssc_data_set_number(data, "subarray1_azimuth", 180);
		ssclib.ssc_data_set_number(data, "subarray1_shade_mode", 1);
		ssclib.ssc_data_set_number(data, "subarray1_dcloss", .1f);
		ssclib.ssc_data_set_number(data, "subarray1_dcwiring_loss", .015f);
		ssclib.ssc_data_set_number(data, "subarray1_tracking_loss", 0);
		ssclib.ssc_data_set_number(data, "subarray1_mismatch_loss", .04f);
		ssclib.ssc_data_set_number(data, "subarray1_nameplate_loss", -0.015f);
		ssclib.ssc_data_set_number(data, "subarray1_diodeconn_loss", 0);

		float[] soilingfactors = new float[] {0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f};
		ssclib.ssc_data_set_array(data, "subarray1_soiling", soilingfactors, 12);

		ssclib.ssc_data_set_number(data, "subarray2_enable", 0);
		ssclib.ssc_data_set_number(data, "subarray2_tilt", 0);
		ssclib.ssc_data_set_number(data, "subarray2_shade_mode", 1);
		ssclib.ssc_data_set_number(data, "subarray2_track_mode", 1);
		ssclib.ssc_data_set_number(data, "subarray2_backtrack", 1);

		ssclib.ssc_data_set_number(data, "subarray3_enable", 0);
		ssclib.ssc_data_set_number(data, "subarray3_tilt", 0);
		ssclib.ssc_data_set_number(data, "subarray3_shade_mode", 1);
		ssclib.ssc_data_set_number(data, "subarray3_track_mode", 1);
		ssclib.ssc_data_set_number(data, "subarray3_backtrack", 1);

		ssclib.ssc_data_set_number(data, "subarray4_enable", 0);
		ssclib.ssc_data_set_number(data, "subarray4_tilt", 0);
		ssclib.ssc_data_set_number(data, "subarray4_shade_mode", 1);
		ssclib.ssc_data_set_number(data, "subarray4_track_mode", 1);
		ssclib.ssc_data_set_number(data, "subarray4_backtrack", 1);

		ssclib.ssc_data_set_number(data, "module_model", 1);
		ssclib.ssc_data_set_number(data, "cec_t_noct", 44.1f);
		ssclib.ssc_data_set_number(data, "cec_area", 1.618f);
		ssclib.ssc_data_set_number(data, "cec_n_s", 60);
		ssclib.ssc_data_set_number(data, "cec_i_sc_ref", 8.37f);
		ssclib.ssc_data_set_number(data, "cec_v_oc_ref", 37.2f);
		ssclib.ssc_data_set_number(data, "cec_i_mp_ref", 7.89f);
		ssclib.ssc_data_set_number(data, "cec_v_mp_ref", 30.4f);
		ssclib.ssc_data_set_number(data, "cec_alpha_sc", .005022f);
		ssclib.ssc_data_set_number(data, "cec_beta_oc", -.1302f);
		ssclib.ssc_data_set_number(data, "cec_a_ref", 1.5731f);
		ssclib.ssc_data_set_number(data, "cec_i_l_ref", 8.373f);
		ssclib.ssc_data_set_number(data, "cec_i_o_ref", (float)4.47e-10);
		ssclib.ssc_data_set_number(data, "cec_r_s", .275f);
		ssclib.ssc_data_set_number(data, "cec_r_sh_ref", 710.44f);
		ssclib.ssc_data_set_number(data, "cec_adjust", 6.704f);
		ssclib.ssc_data_set_number(data, "cec_gamma_r", -0.45f);
		ssclib.ssc_data_set_number(data, "cec_temp_corr_mode", 0);
		ssclib.ssc_data_set_number(data, "cec_standoff", 1);
		ssclib.ssc_data_set_number(data, "cec_height", 0);

		ssclib.ssc_data_set_number(data, "inverter_model", 0);
		ssclib.ssc_data_set_number(data, "inv_snl_ac_voltage", 208f);
		ssclib.ssc_data_set_number(data, "inv_snl_paco", 215f);
		ssclib.ssc_data_set_number(data, "inv_snl_pdco", 225.415261f);
		ssclib.ssc_data_set_number(data, "inv_snl_vdco", 28.89642857f);
		ssclib.ssc_data_set_number(data, "inv_snl_pso", 0.802832927f);
		ssclib.ssc_data_set_number(data, "inv_snl_c0", -8.90E-05f);
		ssclib.ssc_data_set_number(data, "inv_snl_c1", -8.94E-04f);
		ssclib.ssc_data_set_number(data, "inv_snl_c2", -2.38E-02f);
		ssclib.ssc_data_set_number(data, "inv_snl_c3", -8.28E-02f);
		ssclib.ssc_data_set_number(data, "inv_snl_pnt", 0.04f);
		ssclib.ssc_data_set_number(data, "inv_snl_vdcmax", 45f);
		ssclib.ssc_data_set_number(data, "inv_snl_idcmax", 15f);
		ssclib.ssc_data_set_number(data, "inv_snl_mppt_low", 16f);
		ssclib.ssc_data_set_number(data, "inv_snl_mppt_hi", 36f);

		ssclib.ssc_data_set_number(data, "adjust:factor", 1);
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
		assertThat(val.getValue(), greaterThanOrEqualTo(0f));
		
		IntByReference rows = new IntByReference();
		IntByReference cols = new IntByReference();
		Pointer mtx = ssclib.ssc_data_get_matrix(data, "convex_hull", rows, cols);
		assertThat(mtx, not(equalTo(null)));
		assertThat(rows.getValue(), greaterThan(0));
		assertThat(cols.getValue(), greaterThan(0));
	}
}
