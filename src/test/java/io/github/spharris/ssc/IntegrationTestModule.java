package io.github.spharris.ssc;

import static org.junit.Assert.*;

import java.util.List;

import static org.hamcrest.Matchers.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;

public class IntegrationTestModule {
	
	private Module module;
	
	@Before
	public void createModule() {
		module = Module.forName("layoutarea");
	}
	
	@After
	public void freeModule() {
		module.free();
	}
	
	@Test
	public void getNumber() {
		String field = "field";
		float value = 0.5f;
		
		module.setNumber(field, value);
		Optional<Float> result = module.getNumber(field);

		assertThat(result.isPresent(), equalTo(true));
		assertThat(result.get(), equalTo(value));
	}
	
	@Test
	public void getArray() {
		String field = "field";
		float[] values = { 0.5f, 0.5f, 0.5f };
		
		module.setArray(field, values);
		Optional<float[]> result = module.getArray(field);
		
		assertThat(result.isPresent(), equalTo(true));
		assertThat(result.get(), equalTo(values));
	}
	
	@Test
	public void callsGetMatrixProperly() {
		String field = "field";
		float[][] values = {{1, 2}, {3, 4}};
		
		module.setMatrix(field, values);
		Optional<float[][]> result = module.getMatrix(field);
		
		assertThat(result.isPresent(), equalTo(true));
		assertThat(result.get(), equalTo(values));
	}
	
	@Test
	public void getsAllVariables() {
		List<Variable> vars = module.getVariables();
		
		Variable v1 = Variable.buildVariable()
				.varType(Variable.VariableType.INPUT)
				.dataType(Variable.DataType.MATRIX)
				.name("positions")
				.label("Positions within calculataed area")
				.group("layoutarea")
				.required("*")
				.units("")
				.meta("")
				.build();
		
		Variable v2 = Variable.buildVariable()
				.varType(Variable.VariableType.OUTPUT)
				.dataType(Variable.DataType.MATRIX)
				.name("convex_hull")
				.label("Convex hull bounding the region")
				.group("layoutarea")
				.required("*")
				.units("")
				.meta("")
				.build();
		
		Variable v3 = Variable.buildVariable()
				.varType(Variable.VariableType.OUTPUT)
				.dataType(Variable.DataType.NUMBER)
				.name("area")
				.label("Area inside the convex hull")
				.group("layoutarea")
				.required("*")
				.units("")
				.meta("")
				.build();
		
		assertThat(vars, hasSize(3));
		assertThat(vars, contains(v1, v2, v3));
	}
	
	@Test
	public void getAllModules() {
		List<ModuleInfo> modules = Module.getAvailableModules();
		
		assertThat(modules, hasSize(greaterThan(0)));
	}
	
	@Test
	public void executeWithHandler() {
		Module m = Module.forName("pvsamv1");
		populateModuleWithSimData(m);
		
		ExecutionHandler handler = new ExecutionHandler() {

			@Override
			public boolean handleLogMessage(MessageType type, float time, String message) {
				assertThat(type, not(equalTo(null)));
				return true;
			}

			@Override
			public boolean handleProgressUpdate(float percentComplete, float time, String text) {
				assertThat(percentComplete, greaterThanOrEqualTo(0f));
				assertThat(time, greaterThanOrEqualTo(0f));
				return true;
			}
		};
		
		m.execute(handler);
		
		assertThat(m.getNumber("annual_ac_net").get(), greaterThan(0f));
		
		m.free();
	}
	
	@Test
	public void simpleExecute() {
		Module m = Module.forName("pvsamv1");
		populateModuleWithSimData(m);
		
		m.execute();
		
		assertThat(m.getNumber("annual_ac_net").get(), greaterThan(0f));

		m.free();
	}
	
	private static void populateModuleWithSimData(Module m) {
		String weatherFile = IntegrationTestModule.class.getClassLoader().getResource("weather/23129.tm2").getPath();
		m.setString("solar_resource_file", weatherFile);
		m.setArray("albedo", new float[] {0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f});
		
		m.setNumber("system_capacity", 1);
		m.setNumber("modules_per_string", 14);
		m.setNumber("strings_in_parallel", 1);
		m.setNumber("inverter_count", 1);
		
		m.setNumber("ac_loss", 0.1f);
		m.setNumber("acwiring_loss", 0.015f);
		m.setNumber("transformer_loss", 0.02f);
		
		for (int i = 1; i <= 4; i++) {
			m.setNumber("subarray" + i + "_tilt", 20);
			m.setNumber("subarray" + i + "_track_mode", 0);
			m.setNumber("subarray" + i + "_azimuth", 180);
			m.setNumber("subarray" + i + "_shade_mode", 1);
			m.setNumber("subarray" + i + "_dcloss", .1f);
			m.setNumber("subarray" + i + "_dcwiring_loss", .015f);
			m.setNumber("subarray" + i + "_tracking_loss", 0);
			m.setNumber("subarray" + i + "_mismatch_loss", .04f);
			m.setNumber("subarray" + i + "_nameplate_loss", -0.015f);
			m.setNumber("subarray" + i + "_diodeconn_loss", 0);
			m.setArray("subarray" + i + "_soiling", new float[] {0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f});
		}
		
		m.setNumber("module_model", 1);
		m.setNumber("cec_t_noct", 65);
		m.setNumber("cec_area", 0.67f);
		m.setNumber("cec_n_s", 18);
		m.setNumber("cec_i_sc_ref", 7.5f);
		m.setNumber("cec_v_oc_ref", 10.4f);
		m.setNumber("cec_i_mp_ref", 6.6f);
		m.setNumber("cec_v_mp_ref", 8.4f);
		m.setNumber("cec_alpha_sc", 0.003f);
		m.setNumber("cec_beta_oc", -0.04f);
		m.setNumber("cec_a_ref", 0.473f);
		m.setNumber("cec_i_l_ref", 7.545f);
		m.setNumber("cec_i_o_ref", (float)1.94E-09);
		m.setNumber("cec_r_s", 0.094f);
		m.setNumber("cec_r_sh_ref", 15.72f);
		m.setNumber("cec_adjust", 10.6f);
		m.setNumber("cec_gamma_r", -0.5f);
		m.setNumber("cec_temp_corr_mode", 0);
		m.setNumber("cec_standoff", 1);
		m.setNumber("cec_height", 0);
		
		m.setNumber("inverter_model", 1);
		m.setNumber("inv_ds_paco", 225);
		m.setNumber("inv_ds_eff", .965f);
		m.setNumber("inv_ds_pnt", .065f);
		m.setNumber("inv_ds_pso", 250);
		m.setNumber("inv_ds_vdco", 27);
		m.setNumber("inv_ds_vdcmax", 48);
		
		m.setNumber("adjust:factor", 1);
	}
}
